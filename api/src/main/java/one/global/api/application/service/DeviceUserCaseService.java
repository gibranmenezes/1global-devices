package one.global.api.application.service;

import one.global.api.application.port.in.DeviceUseCase;
import one.global.api.application.port.out.DeviceRepository;
import one.global.api.application.validation.registration.DeviceRegistrationValidator;
import one.global.api.Utils.Utils;
import one.global.api.domain.enums.State;
import one.global.api.domain.exception.DeviceNotFoundException;
import one.global.api.domain.model.Device;
import one.global.api.web.dto.PaginatedResponse;

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
            throw new DeviceNotFoundException(String.format("Device with id %s not found", id));
        }
        return device;

    }
    @Override
    public PaginatedResponse<Device> getAllDevices(String brand, State state, int page, int size) {
        return deviceRepository.findAll(brand, state, page, size);
    }

    @Override
    public Device updateDevice(Long id, String name, String brand, State state) {
        Device device = getDeviceById(id);
        device.updateDetails(name, brand);
        device.changeState(state);
        return deviceRepository.save(device);
    }

    @Override
    public Device partiallyUpdateDevice(Long id, String name, String brand, State state) {
        Device device = getDeviceById(id);

        State currentState = device.getState();

        if (Utils.isUpdatingNameAndBrand(name, brand)) {
            device.updateDetails(name, brand);
        }

        if (state != null && state != currentState) {
            device.changeState(state);
        }

       return deviceRepository.save(device);

    }

    @Override
    public void deleteDevice(Long id) {
        Device device = getDeviceById(id);
        device.ensureCanBeDeleted();
        deviceRepository.delete(id);

    }

}
