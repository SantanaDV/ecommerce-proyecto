package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de productos en el eCommerce.
 * Se aplican reglas de negocio para permitir acceso según el rol del usuario.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Obtiene la lista completa de productos disponibles.
     * Cualquier usuario, autenticado o no, puede acceder.
     * @return Lista de productos.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    /**
     * Obtiene un producto específico por su ID.
     * Cualquier usuario puede consultar productos.
     * @param idProducto ID del producto a buscar.
     * @return Producto encontrado o código 404 si no existe.
     */
    @GetMapping("/{idProducto}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Integer idProducto) {
        try {
            Producto producto = productoService.obtenerProductoPorId(idProducto);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }
    }

    /**
     * Crea un nuevo producto en la base de datos.
     * Solo los administradores pueden realizar esta acción.
     * @param producto Objeto con los datos a guardar.
     * @return Producto creado o error si no tiene permisos.
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para crear productos.");
        }

        Producto creado = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Actualiza los datos de un producto.
     * Solo administradores pueden modificar productos.
     * @param idProducto ID del producto a actualizar.
     * @param datosNuevos Datos actualizados del producto.
     * @return Producto actualizado o error si no tiene permisos.
     */
    @PutMapping("/{idProducto}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer idProducto,
                                                @RequestBody Producto datosNuevos) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para actualizar productos.");
        }

        try {
            Producto actualizado = productoService.actualizarProducto(idProducto, datosNuevos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }
    }

    /**
     * Elimina un producto de la base de datos.
     * Solo los administradores pueden eliminar productos.
     * @param idProducto ID del producto a eliminar.
     * @return Mensaje de éxito o error si no tiene permisos.
     */
    @DeleteMapping("/{idProducto}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer idProducto) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para eliminar productos.");
        }

        try {
            productoService.eliminarProducto(idProducto);
            return ResponseEntity.ok("Producto eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }
    }

    /**
     * Obtiene la lista de productos más vendidos en la tienda.
     * Disponible para todos los usuarios.
     * @return Lista con productos más vendidos.
     */
    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<Object[]>> obtenerProductosMasVendidos() {
        return ResponseEntity.ok(productoService.obtenerProductosMasVendidos());
    }

    /**
     * Obtiene los productos más vendidos en el último mes.
     * Disponible para todos los usuarios.
     * @return Lista con productos más vendidos en el último mes.
     */
    @GetMapping("/mas-vendidos-ultimo-mes")
    public ResponseEntity<List<Object[]>> obtenerProductosMasVendidosUltimoMes() {
        return ResponseEntity.ok(productoService.obtenerProductosMasVendidosUltimoMes());
    }

    /**
     * Busca productos dentro de un rango de precio dado.
     * @param precioMin Precio mínimo.
     * @param precioMax Precio máximo.
     * @return Lista de productos dentro del rango especificado.
     */
    @GetMapping("/buscarPorPrecio")
    public ResponseEntity<List<Producto>> buscarProductosPorRangoDePrecio(
            @RequestParam Double precioMin,
            @RequestParam Double precioMax) {

        return ResponseEntity.ok(productoService.buscarProductosPorRangoDePrecio(precioMin, precioMax));
    }

    /**
     * Recupera todos los productos ordenados por su precio de menor a mayor.
     * @return Lista de productos ordenados por precio.
     */
    @GetMapping("/ordenarPorPrecio")
    public ResponseEntity<List<Producto>> listarProductosPorPrecioAscendente() {
        return ResponseEntity.ok(productoService.listarProductosPorPrecioAscendente());
    }

    /**
     * Recupera todos los productos ordenados por su nombre en orden ascendente.
     * @return Lista de productos ordenados alfabéticamente.
     */
    @GetMapping("/ordenarPorNombre")
    public ResponseEntity<List<Producto>> listarProductosPorNombreAscendente() {
        return ResponseEntity.ok(productoService.listarProductosPorNombreAscendente());
    }



    // Añadimos la parte visual al frontend
    @GetMapping("/admin/productos")
    public String adminProductos(Model model) {

        return "admin/productos";
    }

    @GetMapping("/admin/productos/editar/{id}")
    public String editarProductoView(@PathVariable Integer id, Model model) {

        return "admin/form-producto";
    }

    @GetMapping("/admin/productos/nuevo")
    public String nuevoProductoView(Model model) {
        return "admin/form-producto";
    }
    /**
     * Método de utilidad para verificar si el usuario autenticado es ADMIN.
     */
    private boolean esAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return false; // No autenticado o usuario anónimo
        }

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}