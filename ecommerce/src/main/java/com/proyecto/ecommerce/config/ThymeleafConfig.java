package com.proyecto.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

/**
 * Configuración para integrar Spring Security con Thymeleaf.
 */
@Configuration
public class ThymeleafConfig {

    /**
     * Registra el dialecto de Spring Security para poder usar
     * sec:authorize en las plantillas Thymeleaf.
     */
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}