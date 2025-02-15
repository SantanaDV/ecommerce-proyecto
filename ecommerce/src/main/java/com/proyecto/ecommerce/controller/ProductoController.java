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

        /**
         * Obtiene la lista de productos más vendidos en la tienda.
         *
         * @return Lista con el nombre del producto y la cantidad vendida.
         */
        @GetMapping("/mas-vendidos")
        public ResponseEntity<List<Object[]>> obtenerProductosMasVendidos() {
            return ResponseEntity.ok(productoService.obtenerProductosMasVendidos());
        }

        /**
         * Obtiene una lista de pedidos con sus productos asociados.
         *
         * @return Lista con ID del pedido, nombre del usuario, nombre del producto y cantidad comprada.
         */
        @GetMapping("/pedidos-productos")
        public ResponseEntity<List<Object[]>> obtenerPedidosConProductos() {
            return ResponseEntity.ok(productoService.obtenerPedidosConProductos());
        }

        /**
         * Obtiene la lista de productos más vendidos en el último mes.
         *
         * @return Lista con el nombre del producto y la cantidad vendida en el último mes.
         */
        @GetMapping("/mas-vendidos-ultimo-mes")
        public ResponseEntity<List<Object[]>> obtenerProductosMasVendidosUltimoMes() {
            return ResponseEntity.ok(productoService.obtenerProductosMasVendidosUltimoMes());
        }

        /**
         * Obtiene la lista de los 10 productos más caros comprados en la tienda.
         *
         * @return Lista con el nombre del producto y su precio.
         */
        @GetMapping("/mas-caros-comprados")
        public ResponseEntity<List<Object[]>> obtenerProductosMasCarosComprados() {
            return ResponseEntity.ok(productoService.obtenerProductosMasCarosComprados());
        }

        /**
         * Busca productos cuyo nombre contenga una cadena determinada,
         * ignorando mayúsculas y minúsculas.
         *
         * @param nombre Cadena que se busca en el nombre del producto.
         * @return Lista de productos cuyo nombre contenga la cadena especificada.
         */
        @GetMapping("/buscar")
        public ResponseEntity<List<Producto>> buscarProductosPorNombre(@RequestParam String nombre) {
            return ResponseEntity.ok(productoService.buscarProductosPorNombre(nombre));
        }

        /**
         * Busca productos dentro de un rango de precio dado.
         *
         * @param precioMin Precio mínimo.
         * @param precioMax Precio máximo.
         * @return Lista de productos dentro del rango de precio especificado.
         */
        @GetMapping("/buscarPorPrecio")
        public ResponseEntity<List<Producto>> buscarProductosPorRangoDePrecio(
                @RequestParam Double precioMin,
                @RequestParam Double precioMax) {

            return ResponseEntity.ok(productoService.buscarProductosPorRangoDePrecio(precioMin, precioMax));
        }

        /**
         * Busca productos con stock menor a la cantidad dada.
         *
         * @param stock Límite máximo de stock.
         * @return Lista de productos con stock bajo.
         */
        @GetMapping("/buscarPorStock")
        public ResponseEntity<List<Producto>> buscarProductosPorStockBajo(
                @RequestParam Integer stock) {

            return ResponseEntity.ok(productoService.buscarProductosPorStockBajo(stock));
        }

        /**
         * Recupera todos los productos ordenados por su precio de menor a mayor.
         *
         * @return Lista de productos ordenados por precio.
         */
        @GetMapping("/ordenarPorPrecio")
        public ResponseEntity<List<Producto>> listarProductosPorPrecioAscendente() {
            return ResponseEntity.ok(productoService.listarProductosPorPrecioAscendente());
        }

        /**
         * Recupera todos los productos ordenados por su nombre en forma ascendente.
         *
         * @return Lista de productos ordenados alfabéticamente.
         */
        @GetMapping("/ordenarPorNombre")
        public ResponseEntity<List<Producto>> listarProductosPorNombreAscendente() {
            return ResponseEntity.ok(productoService.listarProductosPorNombreAscendente());
        }
}
