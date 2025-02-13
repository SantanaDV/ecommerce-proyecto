package com.proyecto.ecommerce.service;

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

    List<Pedido> listarPedidosPorUsername(String username);

}
