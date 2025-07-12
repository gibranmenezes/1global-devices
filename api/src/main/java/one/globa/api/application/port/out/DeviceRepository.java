package one.globa.api.application.port.out;

import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;

import java.util.List;

public interface DeviceRepository {

    Device save(String name, String brand, String state);
    Device findById(Long id);
    List<Device> findAll(String brand, State state);
    Device update(Long id, String name, String brand, State state);
    Device patchDevice(Long id, String name, String brand, State state);
    void delete(Long id);
}
