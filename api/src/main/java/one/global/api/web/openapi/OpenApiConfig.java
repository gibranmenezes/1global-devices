package one.global.api.web.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("1GLobal API - Device Resources Management")
                        .description("API - Device Resources Management - creations, searches, updates and deletions of devices.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gibran Menezes")
                                .email("gibranquimica@gmail.com")
                        )
                );
    }
}
