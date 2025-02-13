package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.Role;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.RoleRepository;
import com.proyecto.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private  RoleRepository roleRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        // Verificar si el username ya está en uso
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new CustomException("El username ya se encuentra en uso");
        }

        // 1. Asignar roles
        List<Role> rolesAsignados = new ArrayList<>();
        // Por defecto, ROLE_USER
        Optional<Role> rolUser = roleRepository.findByName("ROLE_USER");
        rolUser.ifPresent(rolesAsignados::add);

        // Si el usuario viene marcado como admin => ROLE_ADMIN
        if (usuario.isAdmin()) {
            Optional<Role> rolAdmin = roleRepository.findByName("ROLE_ADMIN");
            rolAdmin.ifPresent(rolesAsignados::add);
        }

        usuario.setRoles(rolesAsignados);

        // 2. Encriptar contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 3. Guardar en base de datos
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
        Usuario existente = obtenerUsuarioPorId(idUsuario);
        usuarioRepository.delete(existente);
    }

    @Override
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow()-> new CustomException("No se encontro el usuario con el username" +
                " " +username);
    }
}
