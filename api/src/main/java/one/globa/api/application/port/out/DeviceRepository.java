package one.globa.api.application.port.out;

import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;
import one.globa.api.presentation.dto.PaginatedResponse;

import java.util.List;

public interface DeviceRepository {

    Device save(Device device);
    Device findById(Long id);
    PaginatedResponse<Device> findAll(String brand, State state, int page, int size);
    void update(Long id, Device device);
    void delete(Long id);
}
