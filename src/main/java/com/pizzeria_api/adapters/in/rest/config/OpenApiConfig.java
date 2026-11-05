package com.pizzeria_api.adapters.in.rest.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pizzeria API - Hexagonal Architecture")
                        .version("1.0.0")
                        .description("REST API para gestión de menú de pizzas y pedidos con idempotencia y arquitectura hexagonal.")
                                        .contact(new Contact()
                                                .name("Adriano")
                                                .url("https://github.com/")));
    }

}
