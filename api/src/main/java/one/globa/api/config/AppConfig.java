package one.globa.api.config;

import one.globa.api.application.port.in.DeviceUseCase;
import one.globa.api.application.port.out.DeviceRepository;
import one.globa.api.application.service.DeviceUserCaseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public DeviceUseCase deviceUseCase(DeviceRepository deviceRepository) {
        return new DeviceUserCaseService(deviceRepository);
    }
}
