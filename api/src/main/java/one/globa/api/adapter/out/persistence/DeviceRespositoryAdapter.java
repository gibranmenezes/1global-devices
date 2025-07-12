package one.globa.api.adapter.out.persistence;

import one.globa.api.application.port.out.DeviceRepository;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;

import java.util.List;

public class DeviceRespositoryAdapter implements DeviceRepository {
    @Override
    public Device save(String name, String brand, String state) {
        return null;
    }

    @Override
    public Device findById(Long id) {
        return null;
    }

    @Override
    public List<Device> findAll(String brand, State state) {
        return List.of();
    }

    @Override
    public Device update(Long id, String name, String brand, State state) {
        return null;
    }

    @Override
    public Device patchDevice(Long id, String name, String brand, State state) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
