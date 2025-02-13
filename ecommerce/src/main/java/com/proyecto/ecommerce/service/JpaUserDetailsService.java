package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que se integra con Spring Security para cargar los detalles de un usuario
 * (UserDetails) desde la base de datos, mapeando la entidad Usuario y su lista de roles.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su username, mapeando sus roles a GrantedAuthority,
     * tal como requiere Spring Security para la autenticación.
     *
     * @param username nombre de usuario que llega al autenticarse.
     * @return un objeto UserDetails con la contraseña encriptada y los roles.
     * @throws UsernameNotFoundException si no se encuentra el usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario en la BD
        Optional<Usuario> userOptional = usuarioRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(
                    String.format("Usuario %s no existe en el sistema", username));
        }

        // Obtenemos la entidad Usuario y sus roles
        Usuario user = userOptional.orElseThrow();
        System.out.println("Details (debug): " + user.getUsername());

        // Convertimos cada Role a un SimpleGrantedAuthority
        List<GrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Construimos el UserDetails que usa Spring Security internamente
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),      // si el usuario está habilitado
                true,                  // accountNonExpired
                true,                  // credentialsNonExpired
                true,                  // accountNonLocked
                roles
        );
    }
}