package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.service.PedidoService;
import com.proyecto.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pedidos.
 * Expone endpoints para crear, listar, obtener, actualizar y eliminar pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    private UsuarioService usuarioService;

    /**
     * Lista todos los pedidos registrados en la base de datos.
     * @return Lista de objetos Pedido.
     */
    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    /**
     * Crea un nuevo pedido. Verifica datos como fecha, total y estado
     * según las validaciones definidas en el servicio.
     * @param pedido Objeto Pedido con la información necesaria.
     * @return El pedido creado, o un error si la validación falla.
     */
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        try {
            // Obtener el username del token
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // Asumir que en tu service de Usuario hay un método para buscar por username
            Usuario userLogueado = usuarioService.obtenerUsuarioPorUsername(username);

            // Asignar el usuario al pedido
            pedido.setUsuario(userLogueado);

            Pedido creado = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene un pedido por su ID.
     * @param idPedido ID del pedido a buscar.
     * @return El pedido si existe, o un código 404 si no se encuentra.
     */
    @GetMapping("/{idPedido}")
    public ResponseEntity<?> obtenerPedido(@PathVariable Integer idPedido) {
        try {
            Pedido pedido = pedidoService.obtenerPedidoPorId(idPedido);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Obtiene un pedido por su ID.
     * @return El pedido si existe, o un código 404 si no se encuentra.
     */
    @GetMapping("/mios")
    public ResponseEntity<?> listarMisPedidos() {
        try {
            // Obtenemos el username del token
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // Buscamos solo los pedidos de ese user
            List<Pedido> pedidosUsuario = pedidoService.listarPedidosPorUsername(username);
            return ResponseEntity.ok(pedidosUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza la información de un pedido existente.
     * @param idPedido ID del pedido a actualizar.
     * @param datosNuevos Datos nuevos (fecha, total, estado, etc.).
     * @return El pedido actualizado, o un error si no se encuentra o válida.
     */
    @PutMapping("/{idPedido}")
    public ResponseEntity<?> actualizarPedido(@PathVariable Integer idPedido,
                                              @RequestBody Pedido datosNuevos) {
        try {
            Pedido actualizado = pedidoService.actualizarPedido(idPedido, datosNuevos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina un pedido de la base de datos.
     * @param idPedido ID del pedido a eliminar.
     * @return Mensaje de éxito si se elimina, o 404 si no se encuentra.
     */
    @DeleteMapping("/{idPedido}")
    public ResponseEntity<?> eliminarPedido(@PathVariable Integer idPedido) {
        try {
            pedidoService.eliminarPedido(idPedido);
            return ResponseEntity.ok("Pedido eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}