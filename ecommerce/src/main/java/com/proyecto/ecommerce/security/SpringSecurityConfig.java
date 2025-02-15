package com.proyecto.ecommerce.security;

import com.proyecto.ecommerce.security.filter.JwtAuthenticationFilter;
import com.proyecto.ecommerce.security.filter.JwtValidationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.PrintWriter;

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
                .authorizeHttpRequests(authz -> authz

                        //  Rutas para registrar usuario y loguearte de forma pública

                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/register").permitAll()

                        //  Gestión de productos (solo administradores)
                        .requestMatchers(HttpMethod.GET, "/api/productos/getProduct/**").hasRole("ADMIN") // Restringe esta ruta
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()



                        //  Rutas para ver todos los usuarios => SOLO ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")              // ver lista completa
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{idUsuario}").hasAnyRole("USER", "ADMIN")

                        // Rutas para crear usuario
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // 6) Rutas para GET un usuario específico =>
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/getUser/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/actualizar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/delete/**").hasRole("ADMIN")

                        // 7) Rutas para pedidos => Autenticación (ROLE_USER o ROLE_ADMIN).
                        //    Por ejemplo, un user normal puede crear su pedido,
                        //    un admin podría ver todos los pedidos, etc.
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("USER", "ADMIN") // Crear pedido
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/id/**").hasAnyRole("ADMIN")//La validacion la controlamos en el controller
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").hasRole("ADMIN") // Ver TODOS los pedidos
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/mios").hasAnyRole("USER", "ADMIN") // Ver solo los pedidos propios
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/{username}").hasAnyRole("USER", "ADMIN")//Ver pedidos utilizando consutlas
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/{username}/total-gastado").hasRole("ADMIN") //Ver total gsatado con consultas y solo rol AMIN
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/cantidad-productos-vendidos").hasRole("ADMIN")//Cantidad de productos vendidos solo para ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/cantidad-pedidos").hasRole("ADMIN")//Total de pedidods por username solo para ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Actualizar
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Borrar


                        // 8) Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // Manejo de excepciones de autenticación y autorización
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint()) // Manejo de 401 Unauthorized
                        .accessDeniedHandler(customAccessDeniedHandler())
                )// Manejo de 403 Forbidden
                // Filtros JWT
                .addFilterBefore(authFilter, JwtValidationFilter.class)
                .addFilter(validationFilter)

                // Deshabilitamos CSRF para una API REST
                .csrf(csrf -> csrf.disable())

                // Sin estado de sesión
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    /**
     * Personaliza la respuesta para 401 Unauthorized (cuando el usuario NO está autenticado).
     */
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("{\"error\": \"No estás autenticado. Por favor, inicia sesión.\"}");
            writer.flush();
        };
    }

    /**
     * Personaliza la respuesta para 403 Forbidden (cuando el usuario no tiene permisos).
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("{\"error\": \"Acceso denegado: No tienes permisos para realizar esta acción.\"}");
            writer.flush();
        };
    }
}