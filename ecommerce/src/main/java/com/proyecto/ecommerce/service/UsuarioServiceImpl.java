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
        // Verificar si el username ya está en uso
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new CustomException("❌ El username ya se encuentra en uso.");
        }

        //  Verificar si ya existe al menos un ADMIN en la base de datos
        boolean hayAdmin = existeAdmin();

        // Asignar el rol por defecto (ROLE_USER)
        List<Role> rolesAsignados = new ArrayList<>();
        roleRepository.findByName("ROLE_USER").ifPresent(rolesAsignados::add);

        //  Obtener información del usuario autenticado (si existe)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean esAnonimo = (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser"));

        // Si NO hay administradores, el primer usuario registrado será ADMIN automáticamente
        if (!hayAdmin) {
            roleRepository.findByName("ROLE_ADMIN").ifPresent(rolesAsignados::add);
            usuario.setAdmin(true);
        } else {
            //  Si ya hay admins, verificar que solo un admin pueda crear más admins
            if (esAnonimo) {
                usuario.setAdmin(false); //  Usuario anónimo NO puede ser ADMIN
            } else {
                String usernameAutenticado = authentication.getName();
                Usuario usuarioAutenticado = obtenerUsuarioPorUsername(usernameAutenticado);

                boolean esAdmin = usuarioAutenticado.getRoles().stream()
                        .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));

                //  Si el usuario autenticado NO es admin y trata de crear un admin, rechazarlo
                if (!esAdmin && usuario.isAdmin()) {
                    throw new CustomException(" Solo un administrador puede crear otros administradores.");
                }
            }
        }

        // 7️ FORZAR QUE SOLO LOS ADMIN CREADOS POR ADMINISTRADORES TENGAN ESE ROL
        if (!usuario.isAdmin()) {
            usuario.setAdmin(false);
            rolesAsignados.removeIf(rol -> rol.getName().equals("ROLE_ADMIN")); //  Eliminar rol de ADMIN si no corresponde
        }

        // 8️⃣ Asignar los roles validados y encriptar la contraseña
        usuario.setRoles(rolesAsignados);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 9️⃣ Guardar y devolver el usuario creado
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
