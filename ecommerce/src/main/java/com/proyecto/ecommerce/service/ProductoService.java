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


    /**
     * Obtiene la lista de productos más vendidos ordenados de mayor a menor cantidad.
     *
     * @return Lista con el nombre del producto y la cantidad vendida.
     */
    List<Object[]> obtenerProductosMasVendidos();

    /**
     * Obtiene una lista con los pedidos y sus productos asociados.
     *
     * @return Lista de objetos con ID del pedido, nombre del usuario, nombre del producto y cantidad comprada.
     */
    List<Object[]> obtenerPedidosConProductos();

    /**
     * Obtiene la lista de productos más vendidos en el último mes.
     *
     * @return Lista con el nombre del producto y la cantidad vendida en el último mes.
     */
    List<Object[]> obtenerProductosMasVendidosUltimoMes();

    /**
     * Obtiene la lista de los 10 productos más caros que han sido comprados en la tienda.
     *
     * @return Lista con el nombre del producto y su precio.
     */
    List<Object[]> obtenerProductosMasCarosComprados();


    /**
     * Busca productos cuyo nombre contenga una cadena determinada,
     * ignorando mayúsculas y minúsculas.
     *
     * @param nombre Cadena que se busca en el nombre del producto.
     * @return Lista de productos cuyo nombre contenga la cadena especificada.
     */
    List<Producto> buscarProductosPorNombre(String nombre);

    /**
     * Busca productos dentro de un rango de precio dado.
     *
     * @param precioMin Precio mínimo a filtrar.
     * @param precioMax Precio máximo a filtrar.
     * @return Lista de productos dentro del rango especificado.
     */
    List<Producto> buscarProductosPorRangoDePrecio(Double precioMin, Double precioMax);


    /**
     * Busca productos con stock menor a la cantidad especificada.
     *
     * @param stock Límite máximo de stock.
     * @return Lista de productos con stock menor al valor dado.
     */
    List<Producto> buscarProductosPorStockBajo(Integer stock);

    /**
     * Recupera todos los productos ordenados por su precio de menor a mayor.
     *
     * @return Lista de productos ordenados por precio.
     */
    List<Producto> listarProductosPorPrecioAscendente();

    /**
     * Recupera todos los productos ordenados por su nombre en forma ascendente.
     *
     * @return Lista de productos ordenados alfabéticamente.
     */
    List<Producto> listarProductosPorNombreAscendente();
}