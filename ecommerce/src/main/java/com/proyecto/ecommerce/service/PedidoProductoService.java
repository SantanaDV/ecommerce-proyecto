package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.PedidoProducto;
import java.util.List;

/**
 * Interfaz para la gestión de PedidoProducto, la entidad asociativa
 * que une Pedido con Producto, permitiendo almacenar información adicional
 * como la cantidad. Define métodos CRUD y búsquedas específicas.
 */
public interface PedidoProductoService {

    /**
     * Lista todos los registros PedidoProducto existentes en la base de datos.
     * @return Lista de PedidoProducto.
     */
    List<PedidoProducto> listarTodos();

    /**
     * Crea un nuevo registro de la tabla intermedia pedido_producto,
     * asociando un pedido con un producto y estableciendo la cantidad.
     * @param pedidoProducto Objeto con los datos necesarios.
     * @return El registro creado y persistido en la base de datos.
     */
    PedidoProducto crear(PedidoProducto pedidoProducto);

    /**
     * Obtiene un registro PedidoProducto por su ID único (si usas un id autogenerado).
     * @param id Clave primaria del PedidoProducto.
     * @return El objeto PedidoProducto si se encuentra, o lanza excepción si no existe.
     */
    PedidoProducto obtenerPorId(Long id);

    /**
     * Actualiza un registro de la tabla pedido_producto, permitiendo
     * modificar campos como la cantidad (u otros, si los tuvieras).
     * @param id ID del registro a actualizar.
     * @param nuevosDatos Objeto con los campos a actualizar.
     * @return El registro ya actualizado.
     */
    PedidoProducto actualizar(Long id, PedidoProducto nuevosDatos);

    /**
     * Elimina un registro de pedido_producto, si existe.
     * @param id ID del registro a eliminar.
     */
    void eliminar(Long id);

    /**
     * Lista todos los registros PedidoProducto asociados a un pedido en particular.
     * @param idPedido ID del pedido.
     * @return Lista de PedidoProducto vinculados a ese pedido.
     */
    List<PedidoProducto> listarPorPedido(Integer idPedido);

    /**
     * Lista todos los registros PedidoProducto asociados a un producto en particular.
     * @param idProducto ID del producto.
     * @return Lista de PedidoProducto vinculados a ese producto.
     */
    List<PedidoProducto> listarPorProducto(Integer idProducto);


    List<PedidoProducto> listarPorUsuario(String username);

    /**
     * Obtiene la relación de un producto en un pedido específico.
     *
     * @param idPedido ID del pedido.
     * @param idProducto ID del producto.
     * @return Lista de registros de PedidoProducto encontrados.
     */
    List<PedidoProducto> obtenerRelacionPedidoProducto(Integer idPedido, Integer idProducto);

}