package com.proyecto.ecommerce.config;

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
public class SecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    /**
     * Bean que define el PasswordEncoder para encriptar contraseñas.
     * Usamos BCrypt para mayor seguridad.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Permite obtener el AuthenticationManager que Spring Security genera,
     * basado en la configuración de usuarios (JpaUserDetailsService, etc.).
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros y las rutas protegidas / permitidas.
     *
     * @param http objeto HttpSecurity para configurar la seguridad.
     * @return la instancia SecurityFilterChain construida.
     * @throws Exception si ocurre un error de configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Instancias de filtros JWT
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authenticationManager());
        JwtValidationFilter validationFilter = new JwtValidationFilter(authenticationManager());

        return http
                .authorizeHttpRequests(authz -> authz

                        // 1) Rutas para registrar usuario de forma pública
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/register").permitAll()

                        // 2) Rutas públicas para ver la lista o un producto (catálogo)
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                        // 3) Rutas para crear, actualizar o eliminar productos => SOLO ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // 4) Rutas para ver todos los usuarios => SOLO ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")              // ver lista completa
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{idUsuario}").hasAnyRole("USER","ADMIN")

                        // 5) Rutas para crear usuario (POST /api/usuarios) => SOLO ADMIN

                        .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN")

                        // 6) Rutas para GET un usuario específico =>
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/getUser/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/actualizar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/delete/**").hasRole("ADMIN")

                        // 7) Rutas para pedidos => Autenticación (ROLE_USER o ROLE_ADMIN).
                        //    Por ejemplo, un user normal puede crear su pedido,
                        //    un admin podría ver todos los pedidos, etc.
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("USER", "ADMIN") // Crear pedido
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").hasRole("ADMIN") // Ver TODOS los pedidos
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/mios").hasAnyRole("USER", "ADMIN") // Ver solo los pedidos propios
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Actualizar
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasAnyRole("USER", "ADMIN") // Borrar

                        // 8) Rutas para pedido-producto => Autenticación (ROLE_USER o ROLE_ADMIN).
                        .requestMatchers("/api/pedido-producto/**").hasAnyRole("USER", "ADMIN")

                        // 9) Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // Filtros JWT
                .addFilter(authFilter)
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