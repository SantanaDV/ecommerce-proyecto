package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.PedidoProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la interfaz PedidoProductoService, que gestiona
 * la relación asociativa entre Pedido y Producto, permitiendo
 * almacenar información adicional como la cantidad.
 */
@Service
public class PedidoProductoServiceImpl implements PedidoProductoService {

    @Autowired
    private PedidoProductoRepository pedidoProductoRepository;

    @Override
    public List<PedidoProducto> listarTodos() {
        return pedidoProductoRepository.findAll();
    }

    @Override
    public PedidoProducto crear(PedidoProducto pedidoProducto) {
        validarCantidad(pedidoProducto.getCantidad());
        // Podrías validar también que pedido != null, producto != null, etc.
        return pedidoProductoRepository.save(pedidoProducto);
    }

    @Override
    public PedidoProducto obtenerPorId(Long id) {
        return pedidoProductoRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "No se encontró el registro PedidoProducto con ID: " + id));
    }

    @Override
    public PedidoProducto actualizar(Long id, PedidoProducto nuevosDatos) {
        PedidoProducto existente = obtenerPorId(id);

        // Validar nuevamente la cantidad
        validarCantidad(nuevosDatos.getCantidad());

        // Actualizar campos permitidos (aquí asumimos que solo la cantidad se suele modificar)
        existente.setCantidad(nuevosDatos.getCantidad());
        // Si necesitas cambiar la asociación de pedido o producto, podrías hacerlo aquí:
        // existente.setPedido(nuevosDatos.getPedido());
        // existente.setProducto(nuevosDatos.getProducto());

        return pedidoProductoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        PedidoProducto existente = obtenerPorId(id);
        pedidoProductoRepository.delete(existente);
    }

    @Override
    public List<PedidoProducto> listarPorPedido(Integer idPedido) {
        // Se asume que en el repositorio hay un método findByPedidoIdPedido
        return pedidoProductoRepository.findByPedidoIdPedido(idPedido);
    }

    @Override
    public List<PedidoProducto> listarPorProducto(Integer idProducto) {
        // Se asume que en el repositorio hay un método findByProductoIdProducto
        return pedidoProductoRepository.findByProductoIdProducto(idProducto);
    }

    @Override
    public List<PedidoProducto> listarPorUsuario(String username) {
        return pedidoProductoRepository.findbyUsername(username);
    }

    /**
     * Valida que la cantidad no sea nula ni negativa.
     * @param cantidad la cantidad a verificar.
     */
    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new CustomException("La cantidad debe ser un número mayor que 0.");
        }
    }
}