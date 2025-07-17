package one.global.api.application.port.out;

import one.global.api.domain.enums.State;
import one.global.api.domain.model.Device;
import one.global.api.web.dto.PaginatedResponse;

import java.util.Optional;

public interface DeviceRepository {

    Device save(Device device);
    Optional<Device> findById(Long id);
    PaginatedResponse<Device> findAll(String brand, State state, int page, int size);
    void delete(Long id);
}
