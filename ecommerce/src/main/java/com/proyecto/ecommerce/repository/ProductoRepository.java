package com.proyecto.ecommerce.repository;

import com.proyecto.ecommerce.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Producto.
 * Proporciona métodos CRUD y consultas derivadas basadas en los atributos de Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Busca productos cuyo nombre contenga una cadena determinada,
     * ignorando mayúsculas y minúsculas. Útil para un buscador de texto parcial.
     *
     * @param nombre Cadena que se busca en el nombre del producto.
     * @return Lista de productos cuyo nombre contenga la cadena especificada.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca productos cuyo precio se encuentre entre un valor mínimo y máximo.
     *
     * @param precioMin Límite inferior del rango de precio (incluido).
     * @param precioMax Límite superior del rango de precio (incluido).
     * @return Lista de productos cuyo precio está dentro del rango especificado.
     */
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);

    /**
     * Encuentra productos que tienen un stock menor a la cantidad especificada.
     *
     * @param stock Cantidad que servirá como límite máximo de stock.
     * @return Lista de productos con stock inferior al valor dado.
     */
    List<Producto> findByStockLessThan(Integer stock);

    /**
     * Recupera todos los productos ordenados por su precio en forma ascendente.
     *
     * @return Lista de productos ordenados por precio de menor a mayor.
     */
    List<Producto> findAllByOrderByPrecioAsc();

    /**
     * Recupera todos los productos ordenados por su nombre en forma ascendente,
     * ignorando mayúsculas y minúsculas en el ordenamiento.
     *
     * @return Lista de productos ordenados alfabéticamente por nombre.
     */
    List<Producto> findAllByOrderByNombreAsc();
    // CONSULTAS JPQL

    /**
     * Verifica si existe un producto con el mismo nombre (sin importar mayúsculas).
     *
     * @param nombre Nombre del producto.
     * @return `true` si existe, `false` en caso contrario.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Obtiene la lista de productos más vendidos ordenados de mayor a menor cantidad.
     *
     * @return Lista con el nombre del producto y la cantidad vendida.
     */
    @Query("SELECT pp.producto.nombre, SUM(pp.cantidad) FROM PedidoProducto pp GROUP BY pp.producto.nombre ORDER BY SUM(pp.cantidad) DESC")
    List<Object[]> FindProductosMasVendidos();

    /**
     * Recupera la lista de pedidos con sus productos asociados.
     *
     * @return Lista con ID del pedido, nombre de usuario, nombre del producto y cantidad comprada.
     */
    @Query("SELECT p.idPedido, p.usuario.username, pr.nombre, pp.cantidad FROM Pedido p JOIN PedidoProducto pp ON p.idPedido = pp.pedido.idPedido JOIN Producto pr ON pp.producto.idProducto = pr.idProducto")
    List<Object[]> findPedidosConProductos();


// CONSULTAS NATIVAS SQL

    /**
     * Encuentra los productos más vendidos en el último mes.
     *
     * @return Lista con el nombre del producto y la cantidad vendida en el último mes.
     */
    @Query(value = "SELECT pr.nombre, SUM(pp.cantidad) FROM pedido_producto pp JOIN producto pr ON pp.id_producto = pr.id_producto JOIN pedido p ON pp.id_pedido = p.id_pedido WHERE p.fecha >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) GROUP BY pr.nombre ORDER BY SUM(pp.cantidad) DESC", nativeQuery = true)
    List<Object[]> findProductosMasVendidosUltimoMes();

    /**
     * Obtiene los productos más caros que han sido comprados en la tienda.
     *
     * @return Lista con el nombre del producto y su precio.
     */
    @Query(value = "SELECT pr.nombre, pr.precio FROM pedido_producto pp JOIN producto pr ON pp.id_producto = pr.id_producto ORDER BY pr.precio DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findProductosMasCarosComprados();





}
