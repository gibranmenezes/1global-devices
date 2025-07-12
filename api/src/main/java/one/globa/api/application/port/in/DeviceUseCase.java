package one.globa.api.application.port.in;

import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;

import java.util.List;

public interface DeviceUseCase {
    Device createDevice(String name, String brand);

    Device getDeviceById(Long id);

    List<Device> getAllDevices(String brand, State state);

    void updateDevice(Long id, String name, String brand, State state);

    void partiallyUpdateDevice(Long id, String name, String brand, State state);

    void deleteDevice(Long id);
}