package com.proyecto.ecommerce.repository;


import com.proyecto.ecommerce.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Pedido.
 * Proporciona métodos CRUD y consultas derivadas mediante JpaRepository.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {



    /**
     * Encuentra todos los pedidos realizados por un usuario dado.
     *
     * @param idUsuario el ID del usuario (cliente)
     * @return Lista de pedidos asociados al usuario
     */
    List<Pedido> findByUsuarioIdUsuario(Integer idUsuario);

    /**
     * Devuelve todos los pedidos asociados a un usuario
     * cuyo username coincida con el valor proporcionado.
     * @param username nombre de usuario (String).
     * @return lista de pedidos del usuario.
     */
    List<Pedido> findByUsuarioUsername(String username);


}
