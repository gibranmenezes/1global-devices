package one.globa.api.adapter;

import one.globa.api.adapter.out.DeviceMapper;
import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import one.globa.api.adapter.out.persistence.DeviceRespositoryAdapter;
import one.globa.api.adapter.out.persistence.JpaDeviceRepository;
import one.globa.api.domain.model.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        assertEquals(expectedDevice.getCreatedAt(), result.getCreatedAt());

    }

    private JpaDeviceEntity getMappedEntity(Device device) {
        JpaDeviceEntity entity = new JpaDeviceEntity();
        entity.setName(device.getName());
        entity.setBrand(device.getBrand());
        entity.setState(device.getState().getValue());
        entity.setCreatedAt(device.getCreatedAt());
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

