package com.proyecto.ecommerce.security;

import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que se integra con Spring Security para cargar los detalles
 * de un usuario (UserDetails) desde la base de datos, mapeando roles.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Buscar el usuario por username
        Optional<Usuario> userOptional = usuarioRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(
                    String.format("Usuario %s no existe en el sistema", username));
        }

        // Obtenemos el usuario y sus roles
        Usuario user = userOptional.orElseThrow();
        // Convertimos roles (Role) a SimpleGrantedAuthority("ROLE_USER"), etc.
        List<GrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Creamos un objeto User de Spring Security con los datos
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),  // enabled
                true,              // accountNonExpired
                true,              // credentialsNonExpired
                true,              // accountNonLocked
                roles
        );
    }
}