package one.global.api.config;

import one.global.api.application.port.in.DeviceUseCase;
import one.global.api.application.port.out.DeviceRepository;
import one.global.api.application.service.DeviceUserCaseService;
import one.global.api.application.validation.creation.DeviceAttributesValidator;
import one.global.api.application.validation.creation.NameBrandValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public DeviceUseCase deviceUseCase(DeviceRepository deviceRepository,
                                       List<DeviceAttributesValidator> deviceAttributesValidators) {
        return new DeviceUserCaseService(deviceRepository, deviceAttributesValidators);
    }

    /*Validators for device registering*/
    @Bean
    @Order(1)
    public NameBrandValidation nameBrandCreationValidation() {
        return new NameBrandValidation();
    }
}
