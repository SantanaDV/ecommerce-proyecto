package com.proyecto.ecommerce.repository;

import com.proyecto.ecommerce.entity.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interfaz para la persistencia de la entidad Usuario.
 * Extiende JpaRepository para proporcionar métodos CRUD básicos y la posibilidad de definir consultas personalizadas.
 */
@Repository //La anotación @Repository indica a Spring que esta interfaz es un componente
            // de la capa de persistencia. Esto permite que Spring la inyecte automáticamente en los servicios donde se requiera.
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> { //El repository extiende de JpaRepositroy lo que propociona metodos basicos para operaciones CRUD
                                                                                //El primer parametro indica la entidad con la que trabaja, la segunda el ripo de clave primaria

    /**
     * Busca un Usuario por su username.
     * @param username el nombre de usuario a buscar
     * @return un Optional que contiene el Usuario si se encuentra, o vacío si no existe
     */
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(@NotBlank(message = "El usuario no puede estar vacío") String username);

    /**
     * Verifica si hay al menos un usuario con el rol ADMIN.
     */
    boolean existsByRoles_Name(String roleName);
}
