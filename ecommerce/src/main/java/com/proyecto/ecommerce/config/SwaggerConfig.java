package com.proyecto.ecommerce.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para Springdoc OpenAPI (Swagger).
 * Genera la documentación interactiva en /swagger-ui.html y /api-docs.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Define la configuración principal de la documentación de la API:
     * título, descripción, versión, etc.
     *
     * Con este bean, Springdoc construye la documentación OpenAPI
     * que se mostrará en /swagger-ui.html
     */
    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce API")
                        .description("API para la gestión de usuarios, productos, pedidos y roles.")
                        .version("v1.0.0")
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación Externa")
                        .url("https://github.com/SantanaDV/ecommerce-proyecto-clase")
                );
    }

    /**
     * Si deseas agrupar endpoints por versión o por módulos,
     * puedes usar GroupedOpenApi. Caso contrario, con un solo bean
     * @Bean public OpenAPI ... es suficiente.
     *
     * Con este bean, se personalizan las rutas que se documentan,
     * y se asigna un nombre de grupo para diferenciarlas.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
