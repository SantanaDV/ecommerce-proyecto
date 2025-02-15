package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de usuarios.
 * Expone endpoints para registrar, listar, obtener, actualizar y eliminar usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos los usuarios registrados en la base de datos.
     * @return Lista de objetos Usuario.
     */
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    /**
     * Crea un nuevo usuario de forma privada.
     * Este endpoint está pensado para un admin (o un rol superior)
     * que desee crear cualquier tipo de usuario (admin o no).
     *
     * @param usuario Objeto Usuario con los datos (se envía en JSON).
     * @param result BindingResult para manejar validaciones.
     * @return Respuesta con el usuario creado o errores de validación.
     */
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody Usuario usuario,
                                          BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        // Verifica si el username existe antes de crear
        if (usuarioService.existePorUsername(usuario.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El username ya está en uso.");
        }

        try {
            Usuario creado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    /**
     * Registra o crea un nuevo usuario de forma pública.
     * Fuerza admin=false para que nadie se dé de alta como administrador.
     *
     * @param usuario Objeto Usuario con los datos (JSON).
     * @param result BindingResult para validaciones.
     * @return Respuesta con el usuario creado o errores de validación.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody Usuario usuario,
                                              BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        // Forzamos para que no pueda autoproclamarse admin
        usuario.setAdmin(false);

        // Comprobamos username duplicado
        if (usuarioService.existePorUsername(usuario.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El username ya está en uso.");
        }

        Usuario creado = usuarioService.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Obtiene un usuario específico por su ID.
     * @param idUsuario ID del usuario a buscar.
     * @return El Usuario si se encuentra, o un 404 si no existe.
     */
    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer idUsuario) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                throw new CustomException("Debes estar autenticado para ver perfiles de usuario.");
            }

            // 1) Obtener al user logueado
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean esAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // 2) Obtener el usuario que se desea ver
            Usuario usuarioBuscado = usuarioService.obtenerUsuarioPorId(idUsuario);


            if (usuarioBuscado == null) {
                throw new CustomException("El usuario con ID " + idUsuario + " no existe.");
            }

            // 3) Verificar:
            //    a) Si es admin => OK
            //    b) Si no es admin => verificar que "usuarioBuscado.getUsername() == username"
            if (!esAdmin && !usuarioBuscado.getUsername().equals(username)) {
                throw new CustomException("No puedes ver el perfil de otro usuario.");
            }

            return ResponseEntity.ok(usuarioBuscado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un usuario específico.
     * @param idUsuario ID del usuario a actualizar.
     * @param datosNuevos Datos nuevos para actualizar al usuario.
     * @return El usuario actualizado, o un 404 si no se encuentra.
     */
    @PutMapping("/actualizar/{idUsuario}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer idUsuario,
                                               @Valid @RequestBody Usuario datosNuevos,
                                               BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        try {
            Usuario actualizado = usuarioService.actualizarUsuario(idUsuario, datosNuevos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina un usuario específico de la base de datos.
     * @param idUsuario ID del usuario a eliminar.
     * @return Un 200 OK si se elimina, o 404 si no se encuentra.
     */
    @DeleteMapping("/delete/{idUsuario}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer idUsuario) {
        try {
            usuarioService.eliminarUsuario(idUsuario);
            return ResponseEntity.ok("Usuario eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Método auxiliar para construir mensajes de error
     * cuando hay fallos en la validación (@Valid).
     * @param result BindingResult que contiene los errores de validación.
     * @return Respuesta con mapa de errores (campo->mensaje).
     */
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
