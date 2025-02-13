package com.proyecto.ecommerce.security;


import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class TokenJwtConfig {

    // Generamos una clave secreta para firmar el token
    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
}