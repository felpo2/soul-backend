package com.project.soul.application;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI soulOpenApi() {
        String securityScheme = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Soul API")
                        .description("API da rede social Soul")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(securityScheme))
                .components(new Components().addSecuritySchemes(
                        securityScheme,
                        new SecurityScheme()
                                .name(securityScheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
