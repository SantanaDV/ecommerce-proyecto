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
        pedidoRepository.delete(existente);
    }

    @Override
    public List<Pedido> listarPedidosPorUsername(String username) {
        return pedidoRepository.findByUsuarioUsername(username);
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
}
