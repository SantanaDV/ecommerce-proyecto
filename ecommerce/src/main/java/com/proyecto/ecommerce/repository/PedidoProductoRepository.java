package com.proyecto.ecommerce.repository;

import com.proyecto.ecommerce.entity.PedidoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, Long> {

    /**
     * Devuelve una lista de registros PedidoProducto asociados a un pedido concreto.
     * @param idPedido El ID del pedido.
     * @return Lista de entidades PedidoProducto vinculadas a ese pedido.
     */
    List<PedidoProducto> findByPedidoIdPedido(Integer idPedido);

    /**
     * Devuelve una lista de registros PedidoProducto asociados a un producto concreto.
     * @param idProducto El ID del producto.
     * @return Lista de entidades PedidoProducto vinculadas a ese producto.
     */
    List<PedidoProducto> findByProductoIdProducto(Integer idProducto);

    /**
     * Busca los registros PedidoProducto de un determinado pedido y un determinado producto.
     *
     * @param idPedido ID del pedido al que pertenece el registro.
     * @param idProducto ID del producto al que pertenece el registro.
     * @return Lista con los registros que coincidan (generalmente será uno, pero se utiliza lista por flexibilidad).
     */
    List<PedidoProducto> findByPedidoIdPedidoAndProductoIdProducto(Integer idPedido, Integer idProducto);
    List<PedidoProducto> findbyUsername(String username);
}
