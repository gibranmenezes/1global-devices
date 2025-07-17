package one.global.api.application.validation;

import one.global.api.domain.exception.InvalidDeviceParameter;

public class NameBrandValidation implements DeviceAttributesValidator {
    @Override
    public void validate(String name, String brand) {
        if((name == null || name.isEmpty()) || (brand == null || brand.isEmpty())) {
            throw new InvalidDeviceParameter("Name and brand must not be empty");
        }
    }
}
