package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la interfaz ProductoService.
 * Incluye validaciones para nombre único, precio y stock no negativos.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto crearProducto(Producto producto) {
        validarNombreUnico(producto.getNombre());
        validarPrecioYStock(producto.getPrecio(), producto.getStock());
        return productoRepository.save(producto);
    }

    @Override
    public Producto obtenerProductoPorId(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new CustomException(
                        "Producto no encontrado con ID: " + idProducto));
    }

    @Override
    public Producto actualizarProducto(Integer idProducto, Producto datosNuevos) {
        Producto existente = obtenerProductoPorId(idProducto);

        // Verificar si el nombre cambió
        if (!existente.getNombre().equalsIgnoreCase(datosNuevos.getNombre())) {
            validarNombreUnico(datosNuevos.getNombre());
        }
        validarPrecioYStock(datosNuevos.getPrecio(), datosNuevos.getStock());

        existente.setNombre(datosNuevos.getNombre());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setPrecio(datosNuevos.getPrecio());
        existente.setStock(datosNuevos.getStock());

        return productoRepository.save(existente);
    }

    @Override
    public void eliminarProducto(Integer idProducto) {
        Producto existente = obtenerProductoPorId(idProducto);
        productoRepository.delete(existente);
    }



    @Override
    public boolean existePorNombreIgnoreCase(String nombre) {
        return productoRepository.existsByNombreIgnoreCase(nombre);
    }


    @Override
    public List<Object[]> obtenerProductosMasVendidos() {
        return productoRepository.FindProductosMasVendidos();
    }

    @Override
    public List<Object[]> obtenerPedidosConProductos() {
        return productoRepository.findPedidosConProductos();
    }

    @Override
    public List<Object[]> obtenerProductosMasVendidosUltimoMes() {
        return productoRepository.findProductosMasVendidosUltimoMes();
    }

    @Override
    public List<Object[]> obtenerProductosMasCarosComprados() {
        return productoRepository.findProductosMasCarosComprados();
    }

    /**
     * Verifica que el nombre no exista en la base de datos,
     * lanzando una excepción si está en uso.
     *
     * @param nombre Nombre de producto a comprobar.
     */
    private void validarNombreUnico(String nombre) {
        if (existePorNombreIgnoreCase(nombre)) {
            throw new CustomException(
                    "Ya existe un producto con el nombre: " + nombre);
        }
    }

    /**
     * Verifica que el precio y el stock sean valores adecuados
     * (no negativos). Lanza CustomException si no cumplen.
     *
     * @param precio Precio a validar.
     * @param stock  Stock a validar.
     */
    private void validarPrecioYStock(Double precio, Integer stock) {
        if (precio == null || precio < 0) {
            throw new CustomException("El precio no puede ser nulo ni negativo.");
        }
        if (stock == null || stock < 0) {
            throw new CustomException("El stock no puede ser nulo ni negativo.");
        }
    }

    @Override
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Producto> buscarProductosPorRangoDePrecio(Double precioMin, Double precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax);
    }

    @Override
    public List<Producto> buscarProductosPorStockBajo(Integer stock) {
        return productoRepository.findByStockLessThan(stock);
    }

    @Override
    public List<Producto> listarProductosPorPrecioAscendente() {
        return productoRepository.findAllByOrderByPrecioAsc();
    }


    @Override
    public List<Producto> listarProductosPorNombreAscendente() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

}