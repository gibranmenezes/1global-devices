package one.globa.api.application.service;

import one.globa.api.application.port.in.DeviceUseCase;
import one.globa.api.application.port.out.DeviceRepository;
import one.globa.api.application.validation.registration.DeviceRegistrationValidator;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;
import one.globa.api.presentation.dto.PaginatedResponse;

import java.util.List;

public class DeviceUserCaseService implements DeviceUseCase {

    private final DeviceRepository deviceRepository;
    private final List<DeviceRegistrationValidator> createValidators;

    public DeviceUserCaseService(DeviceRepository deviceRepository, List<DeviceRegistrationValidator> createValidators) {
        this.deviceRepository = deviceRepository;
        this.createValidators = createValidators;
    }


    @Override
    public Device registerDevice(String name, String brand) {
        createValidators.forEach(v -> v.validate(name, brand));
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
    public PaginatedResponse<Device> getAllDevices(String brand, State state, int page, int size) {
        return deviceRepository.findAll(brand, state, page, size);
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
