package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.Role;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.PedidoRepository;
import com.proyecto.ecommerce.repository.RoleRepository;
import com.proyecto.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Implementación de la interfaz UsuarioService.
 * Contiene la lógica de negocio y válido requisito antes de
 * llamar al repositorio correspondiente.
 */
 @Service
public class UsuarioServiceImpl implements  UsuarioService{
    @Autowired
    private  UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private  RoleRepository roleRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        // 1️⃣ Verificar si el username ya está en uso
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new CustomException("❌ El username ya se encuentra en uso.");
        }

        // 2️⃣ Verificar si ya existe un ADMIN en la base de datos
        boolean hayAdmin = existeAdmin();

        // 3️⃣ Asignar roles por defecto (ROLE_USER)
        List<Role> rolesAsignados = new ArrayList<>();
        Optional<Role> rolUser = roleRepository.findByName("ROLE_USER");
        rolUser.ifPresent(rolesAsignados::add);

        // 4️⃣ Verificar si el usuario autenticado tiene permisos
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean esAnonimo = (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser"));

        // 5️⃣ Si NO hay administradores, el primer usuario será ADMIN
        if (!hayAdmin) {
            Optional<Role> rolAdmin = roleRepository.findByName("ROLE_ADMIN");
            rolAdmin.ifPresent(rolesAsignados::add);
            usuario.setAdmin(true); // Se marca como admin
        } else {
            // 6️⃣ Si ya hay admins, verificar si el usuario autenticado puede crear admins
            if (esAnonimo) {
                usuario.setAdmin(false);  // ❌ Asegurar que el usuario anónimo no pueda ser admin
                throw new CustomException("❌ No tienes permisos para crear administradores.");
            }

            String usernameAutenticado = authentication.getName();
            Usuario usuarioAutenticado = obtenerUsuarioPorUsername(usernameAutenticado);

            // 7️⃣ Si el usuario autenticado NO es admin y trata de crear un admin, rechazarlo
            boolean esAdmin = usuarioAutenticado.getRoles().stream().anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));
            if (!esAdmin && usuario.isAdmin()) {
                usuario.setAdmin(false);  // ❌ Forzar que el usuario no tenga permisos de admin
                throw new CustomException("❌ Solo un administrador puede crear otros administradores.");
            }
        }

        // 8️⃣ 📌 FORZAR ELIMINACIÓN DEL ADMIN SI EL USUARIO NO TIENE PERMISOS
        if (!usuario.isAdmin()) {
            usuario.setAdmin(false);
        }

        // 9️⃣ Asignar los roles verificados y encriptar la contraseña
        usuario.setRoles(rolesAsignados);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }



    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new CustomException("Usuario no encontrado con ID: " + idUsuario));
    }

    @Override
    public Usuario actualizarUsuario(Integer idUsuario, Usuario datosNuevos) {
        Usuario existente = obtenerUsuarioPorId(idUsuario);

        // Actualiza los campos que se deseen permitir modificar
        existente.setNombre(datosNuevos.getNombre());
        existente.setApellido(datosNuevos.getApellido());
        existente.setDireccion(datosNuevos.getDireccion());
        existente.setCorreo(datosNuevos.getCorreo());

// Encriptar la contraseña en caso de que quiera cambiarla
        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(datosNuevos.getPassword()));
        }
        return usuarioRepository.save(existente);
    }

    @Override
    public void eliminarUsuario(Integer idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);

        // 🔥 Primero, eliminar manualmente los pedidos asociados
        usuario.getPedidos().clear();
        usuarioRepository.save(usuario);  // Guardar el cambio antes de eliminar

        // 🔥 Se ejecutará `@PreRemove` automáticamente antes de la eliminación
        usuarioRepository.delete(usuario);
    }

    @Override
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(()-> new CustomException("No se encontro el usuario con el username" +
                " " +username));
    }

    @Override
    public boolean existeAdmin() {
        return usuarioRepository.existsByRoles_Name("ROLE_ADMIN");
    }


}
