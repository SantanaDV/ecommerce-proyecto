package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.service.PedidoService;
import com.proyecto.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pedidos.
 * Expone endpoints para crear, listar, obtener, actualizar y eliminar pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;


    @Autowired
    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

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
     * Obtener pedidos de un usuario por su username (para usuarios y admins).
     */
    @GetMapping("/usuario/{username}")
    public ResponseEntity<?> obtenerPedidosPorUsuario(@PathVariable String username) {
        // Obtener el usuario autenticado
        String authUsername = obtenerUsuarioAutenticado();

        // Si el usuario autenticado NO es ADMIN y trata de ver pedidos de otro usuario, se bloquea
        if (!authUsername.equals(username) && !esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para ver los pedidos de otro usuario.");
        }

        return ResponseEntity.ok(pedidoService.listarPedidosPorUsuario(username));
    }

    /**
     * Obtener el total gastado por un usuario solo para admins.
     */
    @GetMapping("/usuario/{username}/total-gastado")
    public ResponseEntity<?> obtenerTotalGastado(@PathVariable String username) {
        // Solo un ADMIN puede acceder a esta consulta
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para ver esta información.");
        }
        return ResponseEntity.ok(pedidoService.obtenerTotalGastadoPorUsuario(username));
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


    /**
     * Endpoint para obtener la cantidad total de productos comprados por cada usuario.
     * Solo accesible por administradores.
     * @return Lista con el username y la cantidad total de productos comprados.
     */
    @GetMapping("/cantidad-productos-vendidos")
    public ResponseEntity<?> obtenerCantidadProductosVendidosPorUsuario() {
        // Solo los administradores pueden acceder a esta consulta
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para ver esta información.");
        }

        return ResponseEntity.ok(pedidoService.obtenerCantidadProductosVendidosPorUsuario());
    }


    /**
     * Endpoint para obtener la cantidad total de pedidos realizados por cada usuario.
     * Solo accesible por administradores.
     * @return Lista con el username y la cantidad total de pedidos realizados.
     */
    @GetMapping("/cantidad-pedidos")
    public ResponseEntity<?> obtenerCantidadPedidosPorUsuario() {
        // Solo los administradores pueden acceder a esta consulta
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para ver esta información.");
        }

        return ResponseEntity.ok(pedidoService.contarPedidosPorUsuario());
    }

    /**
     * Endpoint para obtener los pedidos de un usuario según su ID.
     * Un usuario solo puede ver sus propios pedidos, o un administrador puede ver cualquier pedido.
     *
     * @param idUsuario ID del usuario cuyos pedidos se quieren ver.
     * @return Lista de pedidos si el usuario tiene permiso.
     */
    @GetMapping("/usuario/id/{idUsuario}")
    public ResponseEntity<?> obtenerPedidosPorIdUsuario(@PathVariable Integer idUsuario) {
        // Obtener el usuario autenticado
        String authUsername = obtenerUsuarioAutenticado();

        // Obtener el usuario buscado por ID
        Usuario usuarioBuscado = usuarioService.obtenerUsuarioPorId(idUsuario);

        // Validar permisos: el usuario autenticado solo puede ver sus propios pedidos o ser ADMIN
        if (!authUsername.equals(usuarioBuscado.getUsername()) && !esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para ver los pedidos de este usuario.");
        }

        return ResponseEntity.ok(pedidoService.listarPedidosPorIdUsuario(idUsuario));
    }


    /**
     * Método de utilidad para obtener el usuario autenticado.
     */
    private String obtenerUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    /**
     * Método de utilidad para verificar si el usuario autenticado es ADMIN.
     */
    private boolean esAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }



}