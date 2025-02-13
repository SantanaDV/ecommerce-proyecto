package com.proyecto.ecommerce.service;

import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.entity.Producto;
import java.util.List;


/**
 * Interfaz que define los métodos para la gestión de productos.
 * Sirve como contrato que la implementación debe cumplir.
 */
public interface ProductoService {

    /**
     * Retorna todos los productos registrados.
     * @return lista de productos.
     */
    List<Producto> listarProductos();

    /**
     * Crea o guarda un producto en la base de datos.
     * @param producto Objeto Producto con los datos a guardar.
     * @return El producto ya persistido en la BD.
     */
    Producto crearProducto(Producto producto);

    /**
     * Obtiene un producto por su ID.
     * @param idProducto La clave primaria del producto.
     * @return El objeto Producto si se encuentra.
     */
    Producto obtenerProductoPorId(Integer idProducto);

    /**
     * Actualiza los datos de un producto existente.
     * @param idProducto la clave primaria del producto a actualizar.
     * @param datosNuevos objeto Producto con campos actualizados.
     * @return El producto actualizado.
     */
    Producto actualizarProducto(Integer idProducto, Producto datosNuevos);

    /**
     * Elimina un producto por ID.
     * @param idProducto ID del producto a eliminar.
     */
    void eliminarProducto(Integer idProducto);



    /**
     * Verifica si existe un producto con el nombre especificado
     * (ignorando mayúsculas y minúsculas).
     * @param nombre Nombre del producto a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existePorNombreIgnoreCase(String nombre);

    List<PedidoProducto> listarPorUsuario (String username);

}