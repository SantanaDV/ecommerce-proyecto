package com.proyecto.ecommerce.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.proyecto.ecommerce.security.TokenJwtConfig.*;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        String token = null;
        String header = request.getHeader(HEADER_AUTHORIZATION);

        // Si la URL empieza con "/api/", usaremos solo el header.
        if (request.getRequestURI().startsWith("/api/")) {
            if (header != null && header.startsWith(PREFIX_TOKEN)) {
                token = header.replace(PREFIX_TOKEN, "").trim();
            }
        } else {
            // Para rutas que no son de la API, primero intentamos el header
            if (header != null && header.startsWith(PREFIX_TOKEN)) {
                token = header.replace(PREFIX_TOKEN, "").trim();
            } else {
                //  y si no está, buscamos en las cookies.
                if (request.getCookies() != null) {
                    for (Cookie cookie : request.getCookies()) {
                        if ("JWT_TOKEN".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }

        if (token == null || token.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                    .verifyWith(SECRET_KEY).build()
                    .parseSignedClaims(token);

            Claims claims = jwsClaims.getPayload();
            String username = claims.getSubject();
            List<String> roles = claims.get("authorities", List.class);

            if (username == null || roles == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(username, null, authorities));

            chain.doFilter(request, response);

        } catch (JwtException e) {
            // Si el token es inválido, se elimina la cookie y se redirige (o se envía error según convenga)
            Cookie expiredCookie = new Cookie("JWT_TOKEN", "");
            expiredCookie.setPath("/");
            expiredCookie.setMaxAge(0);
            response.addCookie(expiredCookie);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token inválido");
        }
    }
}