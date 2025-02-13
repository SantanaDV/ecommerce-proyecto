package com.proyecto.ecommerce.security;

import com.proyecto.ecommerce.security.filter.JwtAuthenticationFilter;
import com.proyecto.ecommerce.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Genera el AuthenticationManager a partir de la configuración.
     */
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define la cadena de filtros y la política de accesos.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // JWT Filters
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authenticationManager());
        JwtValidationFilter validationFilter = new JwtValidationFilter(authenticationManager());

        return http
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/register").permitAll()
                        // Cualquier otra ruta => autenticada
                        .anyRequest().authenticated()
                )
                // Agregar filtros para JWT
                .addFilter(authFilter)
                .addFilter(validationFilter)

                // Deshabilitar CSRF para API REST
                .csrf(csrf -> csrf.disable())

                // Sin estado de sesión => token JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .build();
    }
}