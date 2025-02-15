package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.PedidoRepository;
import com.proyecto.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la interfaz PedidoService,
 * donde se definen validaciones y lógica de negocio para los pedidos.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getUsuario() == null || pedido.getUsuario().getIdUsuario() == null) {
            throw new CustomException("El usuario es obligatorio para crear un pedido.");
        }
        validarFechaYTotal(pedido);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido obtenerPedidoPorId(Integer idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new CustomException(
                        "Pedido no encontrado con ID: " + idPedido));
    }

    @Override
    public Pedido actualizarPedido(Integer idPedido, Pedido datosNuevos) {
        Pedido existente = obtenerPedidoPorId(idPedido);

        // Se puede revalidar fecha, total, estado
        datosNuevos.setIdPedido(idPedido); // Aseguramos que el ID sea el mismo
        validarFechaYTotal(datosNuevos);

        existente.setFecha(datosNuevos.getFecha());
        existente.setTotal(datosNuevos.getTotal());
        existente.setEstado(datosNuevos.getEstado());
        // Si tu modelo contempla asociar un usuario, podrías actualizarlo aquí

        return pedidoRepository.save(existente);
    }

    @Override
    public void eliminarPedido(Integer idPedido) {
        Pedido existente = obtenerPedidoPorId(idPedido);
        // Borrar primero los productos asociados al pedido
        pedidoRepository.deleteProductosByPedido(idPedido);

        // Luego, eliminar el pedido
        pedidoRepository.deleteById(idPedido);
    }

    @Override
    public List<Pedido> listarPedidosPorUsername(String username) {
        return pedidoRepository.findByUsuarioUsername(username);
    }

    @Override
    public List<Pedido> listarPedidosPorUsuario(String username) {
        return pedidoRepository.findByUsuario(username);
    }

    @Override
    public Double obtenerTotalGastadoPorUsuario(String username) {
        List<Object[]> resultado = pedidoRepository.findTotalGastadoPorUsuario(username);
        if (!resultado.isEmpty()) {
            return (Double) resultado.get(0)[1]; // Obtiene el total gastado
        }
        return 0.0; // Retorna 0 si no tiene pedidos
    }

    @Override
    public List<Object[]> obtenerCantidadProductosVendidosPorUsuario() {
        return pedidoRepository.findCantidadProductosVendidosPorUsuario();
    }


    /**
     * Método auxiliar para verificar que la fecha y el total sean válidos.
     * Lanza CustomException si no cumplen los requisitos.
     * @param pedido El objeto Pedido a validar.
     */
    private void validarFechaYTotal(Pedido pedido) {
        if (pedido.getFecha() == null) {
            throw new CustomException("La fecha del pedido no puede ser nula.");
        }
        if (pedido.getTotal() == null || pedido.getTotal() < 0) {
            throw new CustomException("El total del pedido no puede ser nulo ni negativo.");
        }
        if (pedido.getEstado() == null || pedido.getEstado().isBlank()) {
            throw new CustomException("El estado del pedido no puede ser nulo ni vacío.");
        }
    }

    /**
     * Consultamos en la base de datos la cantidad de pedidos por Usuario
     */

    @Override
    public List<Object[]> contarPedidosPorUsuario() {
        return pedidoRepository.CountPedidoPorUsuario();
    }

    /**
     * Consultamos en la base de datos para listar pedidos por su id
     */

    @Override
    public List<Pedido> listarPedidosPorIdUsuario(Integer idUsuario) {
        return pedidoRepository.findByUsuarioIdUsuario(idUsuario);
    }


}
