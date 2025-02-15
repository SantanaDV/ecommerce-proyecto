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
                .authorizeHttpRequests(authz -> authz

                        // 1) Rutas para registrar usuario y loguearte de forma pública
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // 2) Rutas públicas para ver la lista o un producto (catálogo)
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                        // 3) Rutas para crear, actualizar o eliminar productos => SOLO ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // 4) Rutas para ver todos los usuarios => SOLO ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")              // ver lista completa
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{idUsuario}").hasAnyRole("USER","ADMIN")

                        // 5) Rutas para crear usuario (POST /api/usuarios)

                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // 6) Rutas para GET un usuario específico =>
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/getUser/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/actualizar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/delete/**").hasRole("ADMIN")

                        // 7) Rutas para pedidos => Autenticación (ROLE_USER o ROLE_ADMIN).
                        //    Por ejemplo, un user normal puede crear su pedido,
                        //    un admin podría ver todos los pedidos, etc.
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("USER", "ADMIN") // Crear pedido
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/id/**").hasAnyRole("USER", "ADMIN")//La validacion la controlamos en el controller
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").hasRole("ADMIN") // Ver TODOS los pedidos
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/mios").hasAnyRole("USER", "ADMIN") // Ver solo los pedidos propios
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/{username}").hasAnyRole("USER", "ADMIN")//Ver pedidos utilizando consutlas
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/{username}/total-gastado").hasRole("ADMIN") //Ver total gsatado con consultas y solo rol AMIN
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/cantidad-productos-vendidos").hasRole("ADMIN")//Cantidad de productos vendidos solo para ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/cantidad-pedidos").hasRole("ADMIN")//Total de pedidods por username solo para ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Actualizar
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Borrar


                        // 8) Rutas para pedido-producto => Autenticación (ROLE_USER o ROLE_ADMIN).
                        .requestMatchers("/api/pedido-producto/**").hasAnyRole("USER", "ADMIN")

                        // 9) Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

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
}