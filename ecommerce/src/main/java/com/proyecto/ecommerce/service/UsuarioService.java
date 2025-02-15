package com.proyecto.ecommerce.service;


import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.exception.CustomException;

import java.util.List;

/**
 * Interfaz que define los métodos para la gestión de usuarios.
 * Sirve como contrato que la implementación debe cumplir.
 */
public interface UsuarioService {
    /**
     * Crea un nuevo usuario en el sistema.
     * @param usuario objeto con los datos del usuario a crear.
     * @return el usuario creado y persistido en la base de datos.
     */
    Usuario crearUsuario(Usuario usuario);

    /**
     * Retorna todos los usuarios registrados en el sistema.
     * @return lista de todos los usuarios.
     */
    List<Usuario> listarUsuarios();

    /**
     * Busca un usuario por su ID.
     * @param idUsuario la clave primaria del usuario.
     * @return el objeto Usuario si se encuentra o lanza excepción si no existe.
     */
    Usuario obtenerUsuarioPorId(Integer idUsuario);

    /**
     * Actualiza los datos de un usuario.
     * @param idUsuario la clave primaria del usuario a actualizar.
     * @param datosNuevos objeto Usuario con los campos actualizados.
     * @return el usuario actualizado.
     */
    Usuario actualizarUsuario(Integer idUsuario, Usuario datosNuevos);

    /**
     * Elimina un usuario por ID.
     * @param idUsuario ID del usuario a eliminar.
     */
    void eliminarUsuario(Integer idUsuario);

    /**
     * Verifica si existe un usuario con el username especificado.
     * @param username El nombre de usuario que se desea comprobar.
     * @return true si ya existe, false si no.
     */
    boolean existePorUsername(String username);

    /**
     * Busca un usuario en la base de datos a partir de su nombre de usuario (username).
     *
     * @param username Nombre de usuario único registrado en el sistema.
     * @return El objeto Usuario correspondiente al username, si existe.
     * @throws CustomException Si el usuario no es encontrado en la base de datos.
     */
    Usuario obtenerUsuarioPorUsername(String username);

    /**
     * Verifica si ya existe al menos un usuario con el rol de ADMIN.
     * @return true si hay al menos un ADMIN registrado, false si no.
     */
    boolean existeAdmin();

}
