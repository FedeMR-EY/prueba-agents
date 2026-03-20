package com.ey.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Anime Search API")
                                .version("1.0.0")
                                .description(
                                        "API para buscar y guardar información de anime usando Jikan API")
                                .contact(new Contact().name("EY").email("contact@ey.com"))
                                .license(new License().name("Apache 2.0")));
    }
}
