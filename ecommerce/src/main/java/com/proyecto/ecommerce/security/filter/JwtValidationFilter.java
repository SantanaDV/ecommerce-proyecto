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

        // Primero, intentamos obtener el token del header
        if (header != null && header.startsWith(PREFIX_TOKEN)) {
            token = header.replace(PREFIX_TOKEN, "").trim();
        } else {
            // Si no está en el header, lo buscamos en las cookies
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("JWT_TOKEN".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
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

            //  Convertir los roles a Collection<GrantedAuthority>
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());


            //  Autenticamos el usuario con los roles correctos
            SecurityContextHolder.getContext().setAuthentication(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            username, null, authorities));

            chain.doFilter(request, response);

        } catch (JwtException e) {
            // Eliminar la cookie con el token inválido
            Cookie expiredCookie = new Cookie("JWT_TOKEN", "");
            expiredCookie.setPath("/");
            expiredCookie.setMaxAge(0); // Eliminar cookie
            response.addCookie(expiredCookie);

            // Redirigir al usuario a la página de  index
            response.sendRedirect("/");
            return;
        }
    }
}
