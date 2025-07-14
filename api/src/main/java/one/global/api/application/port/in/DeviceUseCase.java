package one.global.api.application.port.in;

import one.global.api.domain.enums.State;
import one.global.api.domain.model.Device;
import one.global.api.presentation.dto.PaginatedResponse;

public interface DeviceUseCase {
    Device registerDevice(String name, String brand);

    Device getDeviceById(Long id);

    PaginatedResponse<Device> getAllDevices(String brand, State state, int page, int size);

    void updateDevice(Long id, String name, String brand, State state);

    void partiallyUpdateDevice(Long id, String name, String brand, State state);

    void deleteDevice(Long id);
}