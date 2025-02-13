package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    /**
     * Controlador REST para la gestión de productos.
     * Expone endpoints para crear, listar, obtener, actualizar y eliminar productos.
     */
    @RestController
    @RequestMapping("/api/productos")
    public class ProductoController {

        @Autowired
        private ProductoService productoService;

        /**
         * Obtiene la lista completa de productos.
         * @return Lista de productos.
         */
        @GetMapping
        public List<Producto> listarProductos() {
            return productoService.listarProductos();
        }

        /**
         * Crea un nuevo producto en la base de datos.
         * @param producto objeto Producto con los datos a guardar.
         * @return El producto creado.
         */
        @PostMapping
        public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
            Producto creado = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        }

        /**
         * Obtiene un producto específico por su ID.
         * @param idProducto ID del producto.
         * @return El producto si se encuentra, o código 404 si no existe.
         */
        @GetMapping("/getProduct/{idProducto}")
        public ResponseEntity<?> obtenerProducto(@PathVariable Integer idProducto) {
            try {
                Producto producto = productoService.obtenerProductoPorId(idProducto);
                return ResponseEntity.ok(producto);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

        /**
         * Actualiza los datos de un producto.
         * @param idProducto ID del producto a actualizar.
         * @param datosNuevos objeto Producto con los datos actualizados.
         * @return El producto actualizado, o un 404 si no existe.
         */
        @PutMapping("/update/{idProducto}")
        public ResponseEntity<?> actualizarProducto(@PathVariable Integer idProducto,
                                                    @RequestBody Producto datosNuevos) {
            try {
                Producto actualizado = productoService.actualizarProducto(idProducto, datosNuevos);
                return ResponseEntity.ok(actualizado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

        /**
         * Elimina un producto de la base de datos.
         * @param idProducto ID del producto a eliminar.
         * @return 200 OK si se elimina, o 404 si no se encuentra.
         */
        @DeleteMapping("/delete/{idProducto}")
        public ResponseEntity<?> eliminarProducto(@PathVariable Integer idProducto) {
            try {
                productoService.eliminarProducto(idProducto);
                return ResponseEntity.ok("Producto eliminado con éxito.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }
}
