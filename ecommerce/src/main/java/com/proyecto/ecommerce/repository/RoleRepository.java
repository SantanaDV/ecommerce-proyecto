package com.proyecto.ecommerce.repository;

import com.proyecto.ecommerce.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    /**
     * Busca un rol por su nombre (ej: "ROLE_USER", "ROLE_ADMIN").
     * @param name nombre del rol buscado.
     * @return un Optional con el Role si existe, o vacío si no se encuentra.
     */
    Optional<Role> findByName(String name);
}
