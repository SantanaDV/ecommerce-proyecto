package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.dto.PedidoRequest;
import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.service.PedidoProductoService;
import com.proyecto.ecommerce.service.PedidoService;
import com.proyecto.ecommerce.service.ProductoService;
import com.proyecto.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de pedidos.
 * Expone endpoints para crear, listar, obtener, actualizar y eliminar pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final PedidoProductoService pedidoProductoService;

    @Autowired
    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService,
                            ProductoService productoService, PedidoProductoService pedidoProductoService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.pedidoProductoService = pedidoProductoService;
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
     * @param pedidoRequest Objeto Pedido con la información necesaria.
     * @return El pedido creado, o un error si la validación falla.
     */
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest pedidoRequest) {
        try {
            // 🔍 Obtener el usuario autenticado
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

            // 📌 Crear un nuevo pedido
            Pedido pedido = new Pedido();
            pedido.setFecha(pedidoRequest.getFecha());
            pedido.setTotal(pedidoRequest.getTotal());
            pedido.setEstado(pedidoRequest.getEstado());
            pedido.setUsuario(usuario);

            // 💾 Guardar el pedido en la base de datos
            Pedido pedidoGuardado = pedidoService.crearPedido(pedido);

            // 📌 Asociar productos al pedido en la tabla intermedia
            List<PedidoProducto> pedidoProductos = pedidoRequest.getProductos().stream().map(productoDTO -> {
                Producto producto = productoService.obtenerProductoPorId(productoDTO.getIdProducto());

                // Crear la relación en PedidoProducto
                PedidoProducto pedidoProducto = new PedidoProducto();
                pedidoProducto.setPedido(pedidoGuardado);
                pedidoProducto.setProducto(producto);
                pedidoProducto.setCantidad(productoDTO.getCantidad());

                return pedidoProducto;
            }).toList();

            // 💾 Guardar la relación en la base de datos
            pedidoProductos.forEach(pedidoProductoService::crear);

            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoGuardado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error al crear el pedido: " + e.getMessage());
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

        if (authUsername == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No estás autenticado. Inicia sesión para ver los pedidos.");
        }
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
        // Verificar si el usuario autenticado es ADMIN
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para actualizar pedidos.");
        }

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
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para eliminar pedidos.");
        }

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println(" No hay autenticación en el contexto.");
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            System.out.println(" Usuario autenticado en SecurityContextHolder: " + username);
            return username;
        } else if (principal instanceof String) {
            System.out.println(" Usuario autenticado (String) en SecurityContextHolder: " + principal);
            return (String) principal;
        }

        System.out.println("No se encontró usuario autenticado en SecurityContextHolder.");
        return null;
    }
    /**
     * Elimina todos los pedidos de un usuario.
     * Solo accesible por administradores.
     * @param idUsuario ID del usuario cuyos pedidos serán eliminados.
     * @return Mensaje de éxito si se eliminan, o error si no se encuentran pedidos.
     */
    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> eliminarPedidosDeUsuario(@PathVariable Integer idUsuario) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para eliminar los pedidos de un usuario.");
        }

        try {
            pedidoService.eliminarPedidosDeUsuario(idUsuario);
            return ResponseEntity.ok("Todos los pedidos del usuario han sido eliminados con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina todos los productos asociados a un pedido específico.
     * Solo accesible por administradores.
     *
     * @param idPedido ID del pedido cuyos productos serán eliminados.
     * @return Mensaje de éxito si la operación se completa correctamente.
     */
    @DeleteMapping("/{idPedido}/productos")
    public ResponseEntity<?> eliminarProductosDePedido(@PathVariable Integer idPedido) {
        // Verificar si el usuario autenticado es ADMIN
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para eliminar productos de un pedido.");
        }

        try {
            pedidoService.eliminarProductosDePedido(idPedido);
            return ResponseEntity.ok("Productos eliminados del pedido con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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