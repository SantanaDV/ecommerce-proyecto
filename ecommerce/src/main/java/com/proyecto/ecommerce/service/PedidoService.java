package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.dto.PedidoRequest;
import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.Usuario;

import java.util.List;

/**
 * Interfaz que define los métodos para la gestión de pedidos en el sistema.
 */
public interface PedidoService {
    /**
     * Lista todos los pedidos registrados en la base de datos.
     * @return Lista de objetos Pedido.
     */
    List<Pedido> listarPedidos();
    /**
     * Crea o guarda un pedido en la base de datos,
     * pudiendo validar detalles como fecha, total y estado.
     * @param pedido Objeto Pedido con los datos a guardar.
     * @return El pedido persistido en la base de datos.
     */
    Pedido crearPedido(Pedido pedido);

    /**
     * Obtiene un pedido por su ID, lanzando excepción si no existe.
     * @param idPedido Clave primaria del pedido.
     * @return El objeto Pedido correspondiente.
     */
    Pedido obtenerPedidoPorId(Integer idPedido);

    /**
     * Actualiza la información de un pedido existente,
     * tales como la fecha, total o estado.
     * @param idPedido ID del pedido a actualizar.
     * @param datosNuevos Objeto Pedido con la información actualizada.
     * @return El pedido ya actualizado.
     */
    Pedido actualizarPedido(Integer idPedido, Pedido datosNuevos);

    /**
     * Elimina un pedido de la base de datos. Lanza excepción si no se encuentra.
     * @param idPedido ID del pedido a eliminar.
     */
    void eliminarPedido(Integer idPedido);

    /**
     * Obtiene la lista de pedidos de un usuario en base a su username.
     *
     * @param username Nombre de usuario.
     * @return Lista de pedidos asociados a ese usuario.
     */
    List<Pedido> listarPedidosPorUsername(String username);

    /**
     * Recupera todos los pedidos realizados por un usuario específico.
     *
     * @param username Nombre de usuario.
     * @return Lista de pedidos del usuario.
     */
    List<Pedido> listarPedidosPorUsuario(String username);

    /**
     * Obtiene el total gastado por un usuario en todos sus pedidos.
     *
     * @param username Nombre de usuario.
     * @return Total gastado por el usuario.
     */
    Double obtenerTotalGastadoPorUsuario(String username);

    /**
     * Obtiene la cantidad total de productos comprados por cada usuario.
     * @return Lista de objetos con el username y la cantidad total de productos comprados.
     */
    List<Object[]> obtenerCantidadProductosVendidosPorUsuario();

    /**
     * Obtiene la cantidad total de pedidos realizados por cada usuario.
     * @return Lista de objetos con el username y la cantidad total de pedidos realizados.
     */
    List<Object[]> contarPedidosPorUsuario();

    /**
     * Obtiene la lista de pedidos realizados por un usuario según su ID.
     * @param idUsuario el ID del usuario.
     * @return lista de pedidos del usuario.
     */
    List<Pedido> listarPedidosPorIdUsuario(Integer idUsuario);

    /**
     * Elimina todos los pedidos de un usuario específico.
     *
     * @param idUsuario ID del usuario cuyos pedidos serán eliminados.
     */
    void eliminarPedidosDeUsuario(Integer idUsuario);


    void eliminarProductosDePedido(Integer idPedido);


    /*
    *
    * Mediante un pedido request y un Usuario crea un Pedido
    *
    * @param usuario usuario al cual hacemos el pedido
    * @param pedidoRequest clase con los datos necesarios para hacer un request de pedido.
    */
    Pedido crearPedidoAdmin(PedidoRequest pedidoRequest, Usuario usuario);


}
