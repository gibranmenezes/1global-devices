package one.global.api.config;

import one.global.api.application.port.in.DeviceUseCase;
import one.global.api.application.port.out.DeviceRepository;
import one.global.api.application.service.DeviceUserCaseService;
import one.global.api.application.validation.registration.DeviceRegistrationValidator;
import one.global.api.application.validation.registration.NameBrandValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public DeviceUseCase deviceUseCase(DeviceRepository deviceRepository,
                                       List<DeviceRegistrationValidator> deviceRegistrationValidators) {
        return new DeviceUserCaseService(deviceRepository, deviceRegistrationValidators);
    }

    /*Validators for device registering*/
    @Bean
    @Order(1)
    public NameBrandValidation nameBrandCreationValidation() {
        return new NameBrandValidation();
    }
}
