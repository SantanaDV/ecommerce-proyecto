package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.PedidoProductoRepository;
import com.proyecto.ecommerce.repository.PedidoRepository;
import com.proyecto.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<PedidoProducto> listarTodos() {
        return pedidoProductoRepository.findAll();
    }

    @Transactional
    public PedidoProducto crear(PedidoProducto pedidoProducto) {
        if (pedidoProducto.getPedido() == null || pedidoProducto.getPedido().getIdPedido() == null) {
            throw new CustomException("El pedido es obligatorio.");
        }
        if (pedidoProducto.getProducto() == null || pedidoProducto.getProducto().getIdProducto() == null) {
            throw new CustomException("El producto es obligatorio.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoProducto.getPedido().getIdPedido())
                .orElseThrow(() -> new CustomException("Pedido no encontrado"));

        Producto producto = productoRepository.findById(pedidoProducto.getProducto().getIdProducto())
                .orElseThrow(() -> new CustomException("Producto no encontrado"));

        pedidoProducto.setPedido(pedido);
        pedidoProducto.setProducto(producto);

        return pedidoProductoRepository.save(pedidoProducto);
    }
    @Override
    public PedidoProducto obtenerPorId(Long id) {
        return pedidoProductoRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "No se encontró el registro PedidoProducto con ID: " + id));
    }

    @Override
    @Transactional
    public PedidoProducto actualizar(Long id, PedidoProducto nuevosDatos) {
        PedidoProducto existente = obtenerPorId(id);

        // Validar  la cantidad
        validarCantidad(nuevosDatos.getCantidad());


        existente.setCantidad(nuevosDatos.getCantidad());

        existente.setCantidad(nuevosDatos.getCantidad());
        return pedidoProductoRepository.save(existente);
    }

    @Override
    @Transactional
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
        return pedidoProductoRepository.findByPedidoUsuarioUsername(username);
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

    @Override
    public List<PedidoProducto> obtenerRelacionPedidoProducto(Integer idPedido, Integer idProducto) {
        return pedidoProductoRepository.findByPedidoIdPedidoAndProductoIdProducto(idPedido, idProducto);
    }
}