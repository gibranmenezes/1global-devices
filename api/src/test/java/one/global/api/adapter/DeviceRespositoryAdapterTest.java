package one.global.api.adapter;

import one.global.api.adapter.out.DeviceMapper;
import one.global.api.adapter.out.entity.JpaDeviceEntity;
import one.global.api.adapter.out.persistence.DeviceRespositoryAdapter;
import one.global.api.adapter.out.persistence.JpaDeviceRepository;
import one.global.api.domain.enums.State;
import one.global.api.domain.exception.DeviceInUseException;
import one.global.api.domain.exception.DeviceNotFoundException;
import one.global.api.domain.model.Device;
import one.global.api.web.dto.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import one.global.api.Utils.Utils;


@ExtendWith(MockitoExtension.class)
class DeviceRespositoryAdapterTest {

    @Mock
    private JpaDeviceRepository jpaDeviceRepository;

    @Mock
    private DeviceMapper deviceMapper;

    private Device device;

    @InjectMocks
    private DeviceRespositoryAdapter deviceRespositoryAdapter;

    @BeforeEach
    void setUp() {
        this.device = new Device("Device1", "BrandA");
    }

    @Test
    @DisplayName("Should successfully convert, persist, and return device with generated ID")
    void save_ShouldConvertAndPersistDeviceWithGeneratedId() {
        var mappedEntity = getMappedEntity(device);
        var savedJpaEntity = getSavedEntity(mappedEntity);
        var expectedDevice = getSavedDevice();

        when(deviceMapper.fromDeviceToJpaDeviceEntity(device)).thenReturn(mappedEntity);
        when(jpaDeviceRepository.save(any(JpaDeviceEntity.class))).thenReturn(savedJpaEntity);
        when(deviceMapper.fromJpaDeviceEntityToDevice(savedJpaEntity)).thenReturn(expectedDevice);

        Device result = deviceRespositoryAdapter.save(device);

        verify(deviceMapper).fromDeviceToJpaDeviceEntity(device);
        verify(jpaDeviceRepository).save(mappedEntity);
        verify(deviceMapper).fromJpaDeviceEntityToDevice(savedJpaEntity);

        assertNotNull(result);
        assertEquals(expectedDevice.getId(), result.getId());
        assertEquals(expectedDevice.getName(), result.getName());
        assertEquals(expectedDevice.getBrand(), result.getBrand());
        assertEquals(expectedDevice.getState(), result.getState());
        assertEquals(expectedDevice.getCreationDate(), result.getCreationDate());
    }

    @Test
    @DisplayName("Should successfully find a device by ID and return it")
    void findById_ShouldReturnDevice_WhenFound() {
        var mappedEntity = getMappedEntity(device);
        var foundJpaEntity = getSavedEntity(mappedEntity);
        var expectedDevice = getSavedDevice();
        Long deviceId = 1L;

        when(jpaDeviceRepository.findById(deviceId)).thenReturn(Optional.of(foundJpaEntity));
        when(deviceMapper.fromJpaDeviceEntityToDevice(foundJpaEntity)).thenReturn(expectedDevice);

        Device result = deviceRespositoryAdapter.findById(deviceId);

        assertNotNull(result);
        assertEquals(expectedDevice.getId(), result.getId());
        assertEquals(expectedDevice.getName(), result.getName());
        assertEquals(expectedDevice.getBrand(), result.getBrand());
        assertEquals(expectedDevice.getState(), result.getState());
        assertEquals(expectedDevice.getCreationDate(), result.getCreationDate());

        verify(jpaDeviceRepository).findById(deviceId);
        verify(deviceMapper).fromJpaDeviceEntityToDevice(foundJpaEntity);
    }

    @Test
    @DisplayName("Should return null when device is not found by ID")
    void findById_ShouldReturnNull_WhenNotFound() {
        Long nonExistentId = 999L;

        when(jpaDeviceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Device result = deviceRespositoryAdapter.findById(nonExistentId);

        assertNull(result);

        verify(jpaDeviceRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should successfully delete a device when found")
    void delete_ShouldSuccessfullyDeleteDevice_WhenFound() {
        Long deviceId = 1L;
        JpaDeviceEntity foundEntity = getSavedEntity(getMappedEntity(device));

        when(jpaDeviceRepository.findById(deviceId)).thenReturn(Optional.of(foundEntity));

        deviceRespositoryAdapter.delete(deviceId);

        verify(jpaDeviceRepository, times(1)).findById(deviceId);
        verify(jpaDeviceRepository, times(1)).delete(foundEntity);
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when attempting to delete a non-existent device")
    void delete_ShouldThrowDeviceNotFoundException_WhenDeviceNotFound() {
        Long nonExistentId = 999L;

        when(jpaDeviceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceRespositoryAdapter.delete(nonExistentId));

        verify(jpaDeviceRepository, times(1)).findById(nonExistentId);
        verify(jpaDeviceRepository, never()).delete(any(JpaDeviceEntity.class));
    }

    @Test
    @DisplayName("Should return paginated devices when brand and state are provided")
    void findAll_ShouldReturnPaginatedDevices_WhenBrandAndStateAreProvided() {
        String brand = "BrandA";
        State state = State.AVAILABLE;
        int page = 0;
        int size = 1;

        List<JpaDeviceEntity> jpaEntities = List.of(getSavedEntity(getMappedEntity(device)));
        Page<JpaDeviceEntity> jpaPage = new PageImpl<>(jpaEntities);

        Device expectedDevice = getSavedDevice();
        List<Device> expectedDevices = List.of(expectedDevice);

        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getNameFromState(state)).thenReturn(state.name());

            when(jpaDeviceRepository.findAllByBrandOrState(eq(brand), eq(state.name()), any(Pageable.class))).thenReturn(jpaPage);
            when(deviceMapper.fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class))).thenReturn(expectedDevice);

            PaginatedResponse<Device> result = deviceRespositoryAdapter.findAll(brand, state, page, size);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(expectedDevices, result.getContent());
            assertEquals(page, result.getPageNumber());
            assertEquals(size, result.getPageSize());
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());

            verify(jpaDeviceRepository, times(1)).findAllByBrandOrState(eq(brand), eq(state.name()), any(Pageable.class));
            verify(deviceMapper, times(1)).fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class));
            mockedUtils.verify(() -> Utils.getNameFromState(state), times(1));
        }
    }

    @Test
    @DisplayName("Should return paginated devices when only brand is provided")
    void findAll_ShouldReturnPaginatedDevices_WhenOnlyBrandIsProvided() {
        String brand = "BrandA";
        int page = 0;
        int size = 10;

        List<JpaDeviceEntity> jpaEntities = List.of(getSavedEntity(getMappedEntity(device)));
        Page<JpaDeviceEntity> jpaPage = new PageImpl<>(jpaEntities);

        Device expectedDevice = getSavedDevice();
        List<Device> expectedDevices = List.of(expectedDevice);

        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getNameFromState(null)).thenReturn(null);

            when(jpaDeviceRepository.findAllByBrandOrState(eq(brand), eq(null), any(Pageable.class))).thenReturn(jpaPage);
            when(deviceMapper.fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class))).thenReturn(expectedDevice);

            PaginatedResponse<Device> result = deviceRespositoryAdapter.findAll(brand, null, page, size);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(expectedDevices, result.getContent());

            verify(jpaDeviceRepository, times(1)).findAllByBrandOrState(eq(brand), eq(null), any(Pageable.class));
            verify(deviceMapper, times(1)).fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class));
            mockedUtils.verify(() -> Utils.getNameFromState(null), times(1));
        }
    }

    @Test
    @DisplayName("Should return paginated devices when only state is provided")
    void findAll_ShouldReturnPaginatedDevices_WhenOnlyStateIsProvided() {
        State state = State.IN_USE;
        int page = 0;
        int size = 10;

        List<JpaDeviceEntity> jpaEntities = List.of(getSavedEntity(getMappedEntity(device)));
        Page<JpaDeviceEntity> jpaPage = new PageImpl<>(jpaEntities);

        Device expectedDevice = getSavedDevice();
        List<Device> expectedDevices = List.of(expectedDevice);

        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getNameFromState(state)).thenReturn(state.name());

            when(jpaDeviceRepository.findAllByBrandOrState(eq(null), eq(state.name()), any(Pageable.class))).thenReturn(jpaPage);
            when(deviceMapper.fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class))).thenReturn(expectedDevice);

            PaginatedResponse<Device> result = deviceRespositoryAdapter.findAll(null, state, page, size);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(expectedDevices, result.getContent());

            verify(jpaDeviceRepository, times(1)).findAllByBrandOrState(eq(null), eq(state.name()), any(Pageable.class));
            verify(deviceMapper, times(1)).fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class));
            mockedUtils.verify(() -> Utils.getNameFromState(state), times(1));
        }
    }

    @Test
    @DisplayName("Should return paginated devices when no filters are provided")
    void findAll_ShouldReturnPaginatedDevices_WhenNoFiltersAreProvided() {
        int page = 0;
        int size = 10;

        List<JpaDeviceEntity> jpaEntities = List.of(getSavedEntity(getMappedEntity(device)));
        Page<JpaDeviceEntity> jpaPage = new PageImpl<>(jpaEntities);

        Device expectedDevice = getSavedDevice();
        List<Device> expectedDevices = List.of(expectedDevice);

        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getNameFromState(null)).thenReturn(null);

            when(jpaDeviceRepository.findAllByBrandOrState(eq(null), eq(null), any(Pageable.class))).thenReturn(jpaPage);
            when(deviceMapper.fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class))).thenReturn(expectedDevice);

            PaginatedResponse<Device> result = deviceRespositoryAdapter.findAll(null, null, page, size);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(expectedDevices, result.getContent());

            verify(jpaDeviceRepository, times(1)).findAllByBrandOrState(eq(null), eq(null), any(Pageable.class));
            verify(deviceMapper, times(1)).fromJpaDeviceEntityToDevice(any(JpaDeviceEntity.class));
            mockedUtils.verify(() -> Utils.getNameFromState(null), times(1));
        }

    }


    private JpaDeviceEntity getMappedEntity(Device device) {
        JpaDeviceEntity entity = new JpaDeviceEntity();
        entity.setName(device.getName());
        entity.setBrand(device.getBrand());
        entity.setState(device.getState().name());
        entity.setCreationDate(device.getCreationDate());
        return entity;
    }

    private JpaDeviceEntity getSavedEntity(JpaDeviceEntity entity) {
        entity.setId(1L);
        return entity;
    }
    private Device getSavedDevice() {
        Device savedDevice = new Device("Device1", "BrandA");
        savedDevice.setId(1L);
       return savedDevice;
    }
}
