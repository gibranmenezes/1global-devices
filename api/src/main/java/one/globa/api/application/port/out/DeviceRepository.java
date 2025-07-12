package one.globa.api.application.port.out;

import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;

import java.util.List;

public interface DeviceRepository {

    Device save(Device device);
    Device findById(Long id);
    List<Device> findAll(String brand, State state);
    void update(Long id, Device device);
    void delete(Long id);
}
