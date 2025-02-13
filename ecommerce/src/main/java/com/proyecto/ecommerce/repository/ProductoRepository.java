package com.proyecto.ecommerce.repository;

import com.proyecto.ecommerce.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
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


    boolean existsByNombreIgnoreCase(String nombre);
}
