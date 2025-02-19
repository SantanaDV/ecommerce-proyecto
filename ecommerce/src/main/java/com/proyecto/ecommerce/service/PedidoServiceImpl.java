package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.dto.PedidoRequest;
import com.proyecto.ecommerce.dto.ProductoCantidadDTO;
import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.PedidoProductoRepository;
import com.proyecto.ecommerce.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la interfaz PedidoService,
 * donde se definen validaciones y lógica de negocio para los pedidos.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoProductoRepository pedidoProductoRepository;

    @Autowired
    private ProductoService productoService;


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
        //  Eliminar manualmente la relación con PedidoProducto antes de eliminar el pedido
        existente.getPedidoProductos().clear();
        pedidoRepository.save(existente);

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

    /**
     * Elimina todos los pedidos de un usuario y sus productos relacionados.
     * Se ejecuta dentro de una transacción para asegurar la consistencia.
     */
    @Transactional
    @Override
    public void eliminarPedidosDeUsuario(Integer idUsuario) {
        // Buscar los pedidos del usuario
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdUsuario(idUsuario);

        if (pedidos.isEmpty()) {
            throw new CustomException("El usuario con ID " + idUsuario + " no tiene pedidos.");
        }

        // Primero, eliminar los productos asociados a cada pedido antes de eliminar los pedidos
        for (Pedido pedido : pedidos) {
            pedidoProductoRepository.deleteProductosByPedido(pedido.getIdPedido());
        }

        // Ahora, eliminar los pedidos
        pedidoRepository.deletePedidosByUsuario(idUsuario);
    }

    @Override
    @Transactional
    public void eliminarProductosDePedido(Integer idPedido) {
        // Verificamos si el pedido existe
        Pedido pedido = obtenerPedidoPorId(idPedido);

        if (pedido == null) {
            throw new CustomException("No se encontró un pedido con ID: " + idPedido);
        }

        // Eliminamos los productos asociados al pedido
        pedidoRepository.deleteProductosByPedido(idPedido);
    }

    @Override
    @Transactional
    public Pedido crearPedidoAdmin(PedidoRequest pedidoRequest, Usuario usuario) {
        // Validar datos básicos del pedido
        if (pedidoRequest.getFecha() == null) {
            throw new CustomException("La fecha del pedido es obligatoria.");
        }
        if (pedidoRequest.getTotal() == null || pedidoRequest.getTotal() < 0) {
            throw new CustomException("El total del pedido no puede ser nulo ni negativo.");
        }
        if (pedidoRequest.getEstado() == null || pedidoRequest.getEstado().isBlank()) {
            throw new CustomException("El estado del pedido no puede ser nulo ni vacío.");
        }
        if (pedidoRequest.getProductos() == null || pedidoRequest.getProductos().isEmpty()) {
            throw new CustomException("Debe incluir al menos un producto en el pedido.");
        }

        // Crear el objeto Pedido
        Pedido pedido = new Pedido();
        pedido.setFecha(pedidoRequest.getFecha());
        pedido.setTotal(pedidoRequest.getTotal());
        pedido.setEstado(pedidoRequest.getEstado());
        pedido.setUsuario(usuario);

        // Guardar el pedido para obtener su ID
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Procesar cada producto del pedido
        for (ProductoCantidadDTO pcDTO : pedidoRequest.getProductos()) {
            // Obtener el producto
            Producto producto = productoService.obtenerProductoPorId(pcDTO.getIdProducto());

            // Validar stock disponible
            if (producto.getStock() < pcDTO.getCantidad()) {
                throw new CustomException("No hay suficiente stock para el producto: " + producto.getNombre());
            }

            // Actualizar el stock del producto
            producto.setStock(producto.getStock() - pcDTO.getCantidad());
            productoService.actualizarProducto(producto.getIdProducto(), producto);

            // Crear la relación en la entidad intermedia (PedidoProducto)
            PedidoProducto pedidoProducto = new PedidoProducto();
            pedidoProducto.setPedido(pedidoGuardado);
            pedidoProducto.setProducto(producto);
            pedidoProducto.setCantidad(pcDTO.getCantidad());

            // Guardar la relación en la base de datos
            pedidoProductoRepository.save(pedidoProducto);
        }

        return pedidoGuardado;
    }

}
