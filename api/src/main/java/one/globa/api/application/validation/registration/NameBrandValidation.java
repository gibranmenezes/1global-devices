package one.globa.api.application.validation.registration;

import one.globa.api.domain.exception.CreateDeviceException;

public class NameBrandValidation implements DeviceRegistrationValidator {
    @Override
    public void validate(String name, String brand) {
        if((name == null || name.isEmpty()) || (brand == null || brand.isEmpty())) {
            throw new CreateDeviceException("Name and brand must not be empty");
        }
    }
}
