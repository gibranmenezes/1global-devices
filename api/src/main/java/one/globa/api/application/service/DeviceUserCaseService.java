package one.globa.api.application.service;

import one.globa.api.application.port.in.DeviceUseCase;
import one.globa.api.application.port.out.DeviceRepository;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;

import java.util.List;

public class DeviceUserCaseService implements DeviceUseCase {

    private final DeviceRepository deviceRepository;

    public DeviceUserCaseService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }


    @Override
    public Device createDevice(String name, String brand) {
        Device device = new Device(name, brand);
        return deviceRepository.save(device);
    }

    @Override
    public Device getDeviceById(Long id) {
        Device device = deviceRepository.findById(id);
        if (device == null) {
            throw new RuntimeException(String.format("Device with id %s not found", id));
        }
        return device;

    }
    @Override
    public List<Device> getAllDevices(String brand, State state) {
        return List.of();
    }

    @Override
    public void updateDevice(Long id, String name, String brand, State state) {

    }

    @Override
    public void partiallyUpdateDevice(Long id, String name, String brand, State state) {

    }

    @Override
    public void deleteDevice(Long id) {

    }
}
