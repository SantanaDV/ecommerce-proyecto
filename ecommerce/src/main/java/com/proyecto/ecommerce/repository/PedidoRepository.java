package com.proyecto.ecommerce.repository;


import com.proyecto.ecommerce.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // CONSULTAS JPQL

    /**
     * Recupera todos los pedidos realizados por un usuario específico
     * mediante su username.
     *
     * @param username Nombre de usuario del cliente.
     * @return Lista de pedidos del usuario.
     */
    @Query("SELECT p FROM Pedido p WHERE p.usuario.username = :username")
    List<Pedido> findByUsuario(@Param("username") String username);

    /**
     * Cuenta la cantidad total de pedidos realizados por cada usuario.
     *
     * @return Lista de arreglos donde cada elemento contiene el username y la cantidad de pedidos.
     */
    @Query("SELECT p.usuario.username, COUNT(p) FROM Pedido p GROUP BY p.usuario.username")
    List<Object[]> CountPedidoPorUsuario();


// CONSULTAS NATIVAS SQL

    /**
     * Calcula el total gastado por un usuario en todos sus pedidos.
     *
     * @param username Nombre de usuario del cliente.
     * @return Lista con el username y el total gastado.
     */
    @Query(value = "SELECT u.username, SUM(p.total) AS total_gastado FROM usuario u JOIN pedido p ON u.id_usuario = p.id_usuario WHERE u.username = :username GROUP BY u.username", nativeQuery = true)
    List<Object[]> findTotalGastadoPorUsuario(@Param("username") String username);

    /**
     * Obtiene la cantidad total de productos vendidos por usuario.
     *
     * @return Lista con el username y la cantidad total de productos vendidos.
     */
    @Query(value = "SELECT u.username, SUM(pp.cantidad) FROM usuario u JOIN pedido p ON u.id_usuario = p.id_usuario JOIN pedido_producto pp ON p.id_pedido = pp.id_pedido GROUP BY u.username", nativeQuery = true)
    List<Object[]> findCantidadProductosVendidosPorUsuario();


// CONSULTAS DE BORRADO EN CASCADA

    /**
     * Elimina todos los pedidos de un usuario específico.
     *
     * @param idUsuario ID del usuario cuyos pedidos serán eliminados.
     */
    @Modifying
    @Query("DELETE FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario")
    void deletePedidosByUsuario(@Param("idUsuario") Integer idUsuario);

    /**
     * Elimina todos los productos asociados a un pedido específico.
     *
     * @param idPedido ID del pedido cuyos productos serán eliminados.
     */
    @Modifying
    @Query("DELETE FROM PedidoProducto pp WHERE pp.pedido.idPedido = :idPedido")
    void deleteProductosByPedido(@Param("idPedido") Integer idPedido);

}
