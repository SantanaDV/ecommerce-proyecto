package com.proyecto.ecommerce.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Esta clase se usa para deserializar los roles (GrantedAuthority)
 * que vienen en el payload del token JWT.
 */
public abstract class SimpleGrantedAuthorityJsonCreator {

    /**
     * Recibe la cadena "authority" (por ejemplo, "ROLE_USER")
     * y la asigna al constructor de SimpleGrantedAuthority.
     */
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
        // No implementamos nada aquí;
        // Jackson llama al constructor de SimpleGrantedAuthority internamente
    }
}