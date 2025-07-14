package one.global.api.adapter;

import one.global.api.adapter.out.DeviceMapper;
import one.global.api.adapter.out.entity.JpaDeviceEntity;
import one.global.api.adapter.out.persistence.DeviceRespositoryAdapter;
import one.global.api.adapter.out.persistence.JpaDeviceRepository;
import one.global.api.domain.model.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private JpaDeviceEntity getMappedEntity(Device device) {
        JpaDeviceEntity entity = new JpaDeviceEntity();
        entity.setName(device.getName());
        entity.setBrand(device.getBrand());
        entity.setState(device.getState().getValue());
        entity.setCreationDate(device.getCreationDate());
        return entity;
    }

    private JpaDeviceEntity getSavedEntity(JpaDeviceEntity entity) {
        entity.setId(1L);
        return entity;
    }
    private Device getSavedDevice() {
        Device savedDevice = this.device;
        savedDevice.setId(1L);
        return savedDevice;
    }

}

