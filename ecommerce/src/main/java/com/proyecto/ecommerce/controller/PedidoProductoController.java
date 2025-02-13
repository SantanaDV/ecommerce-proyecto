package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.service.PedidoProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la entidad asociativa PedidoProducto,
 * que vincula Pedido con Producto y gestiona información adicional
 * como la cantidad.
 */
@RestController
@RequestMapping("/api/pedido-producto")
public class PedidoProductoController {

    @Autowired
    private PedidoProductoService pedidoProductoService;

    /**
     * Lista todos los registros de pedido_producto en la base de datos.
     * @return Lista de PedidoProducto.
     */
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            // Obtenemos el username y los roles del usuario logueado
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean esAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            // Si es admin => listar todos
            if (esAdmin) {
                return ResponseEntity.ok(pedidoProductoService.listarTodos());
            } else {
                // Si es user => listar SOLO los registros de sus pedidos
                // Lógica: buscar sus pedidos y traer la tabla intermedia correspondiente, o
                // usar un método en el service que filtre directamente por username
                List<PedidoProducto> registrosUser = pedidoProductoService.listarPorUsuario(username);
                return ResponseEntity.ok(registrosUser);
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /**
     * Crea un nuevo registro asociando un pedido con un producto y
     * estableciendo la cantidad. Aplica validaciones como cantidad > 0.
     * @param pedidoProducto Objeto con los datos del nuevo registro.
     * @return El registro creado, o un error si falla la validación.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PedidoProducto pedidoProducto) {
        try {
            PedidoProducto creado = pedidoProductoService.crear(pedidoProducto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene un registro de pedido_producto por su ID (si usas un ID autogenerado).
     * @param id ID del registro en la tabla intermedia.
     * @return El registro encontrado, o un 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            PedidoProducto pedidoProducto = pedidoProductoService.obtenerPorId(id);
            return ResponseEntity.ok(pedidoProducto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Actualiza un registro de pedido_producto, por ejemplo para cambiar la cantidad.
     * @param id ID del registro a actualizar.
     * @param nuevosDatos Objeto con la información a modificar.
     * @return El registro ya actualizado, o un error si no se encuentra o valida.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody PedidoProducto nuevosDatos) {
        try {
            PedidoProducto actualizado = pedidoProductoService.actualizar(id, nuevosDatos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina un registro de pedido_producto si existe.
     * @param id ID del registro a eliminar.
     * @return Mensaje de éxito o error si no se encuentra.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            pedidoProductoService.eliminar(id);
            return ResponseEntity.ok("Registro pedido-producto eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Lista todos los registros asociados a un pedido concreto.
     * @param idPedido ID del pedido.
     * @return Lista de PedidoProducto vinculados a ese pedido.
     */
    @GetMapping("/por-pedido/{idPedido}")
    public ResponseEntity<?> listarPorPedido(@PathVariable Integer idPedido) {
        List<PedidoProducto> lista = pedidoProductoService.listarPorPedido(idPedido);
        return ResponseEntity.ok(lista);
    }

    /**
     * Lista todos los registros asociados a un producto concreto.
     * @param idProducto ID del producto.
     * @return Lista de PedidoProducto vinculados a ese producto.
     */
    @GetMapping("/por-producto/{idProducto}")
    public ResponseEntity<?> listarPorProducto(@PathVariable Integer idProducto) {
        List<PedidoProducto> lista = pedidoProductoService.listarPorProducto(idProducto);
        return ResponseEntity.ok(lista);
    }
}
