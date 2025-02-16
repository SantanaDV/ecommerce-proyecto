package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.PedidoProducto;
import com.proyecto.ecommerce.entity.Producto;
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

/**
 * Controlador REST para la entidad asociativa PedidoProducto,
 * que vincula Pedido con Producto y gestiona información adicional
 * como la cantidad.
 */
@RestController
@RequestMapping("/api/pedido-producto")
public class PedidoProductoController {


    private final PedidoProductoService pedidoProductoService;
    private final PedidoService pedidoService;
    private final  ProductoService productoService;
    @Autowired
    public PedidoProductoController(PedidoProductoService pedidoProductoService, PedidoService pedidoService, ProductoService productoService) {
        this.pedidoProductoService = pedidoProductoService;
        this.pedidoService = pedidoService;
        this.productoService =  productoService;
    }



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
            //  Obtener el usuario autenticado
            String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

            //  Obtener el pedido desde la base de datos
            Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoProducto.getPedido().getIdPedido());
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" Pedido no encontrado.");
            }

            //  Verificar si el usuario autenticado es el dueño del pedido
            if (!pedido.getUsuario().getUsername().equals(usernameAutenticado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(" No tienes permisos para modificar este pedido.");
            }

            //  Obtener el producto desde la base de datos
            Producto producto = productoService.obtenerProductoPorId(pedidoProducto.getProducto().getIdProducto());
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" Producto no encontrado.");
            }

            //  Validar que la cantidad sea mayor a 0
            if (pedidoProducto.getCantidad() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" La cantidad debe ser mayor a 0.");
            }

            // Asignar los objetos validados
            pedidoProducto.setPedido(pedido);
            pedidoProducto.setProducto(producto);

            //  Guardar en la base de datos
            PedidoProducto creado = pedidoProductoService.crear(pedidoProducto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(" Error: " + e.getMessage());
        }
    }


    /**
     * Obtiene un registro de pedido_producto por su ID (si usas un ID autogenerado).
     * @param id ID del registro en la tabla intermedia.
     * @return El registro encontrado, o un 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        // Verificar si el usuario autenticado es ADMIN
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para acceder a este recurso.");
        }

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
        // Verificar si el usuario autenticado es ADMIN
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para actualizar este registro.");
        }

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
        // Verificar si el usuario autenticado es ADMIN
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para eliminar este registro.");
        }

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
        // Obtener el usuario autenticado
        String usernameAutenticado = obtenerUsuarioAutenticado();

        // Si no está autenticado, bloquear
        if (usernameAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Debes estar autenticado para ver los productos de un pedido.");
        }

        // Obtener el pedido de la base de datos
        Pedido pedido = pedidoService.obtenerPedidoPorId(idPedido);

        // Si el pedido no existe, devolver error
        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado.");
        }

        // Si el usuario es ADMIN, puede ver todos los pedidos
        if (esAdmin()) {
            return ResponseEntity.ok(pedidoProductoService.listarPorPedido(idPedido));
        }

        // Si el usuario autenticado es dueño del pedido, permitir el acceso
        if (pedido.getUsuario().getUsername().equals(usernameAutenticado)) {
            return ResponseEntity.ok(pedidoProductoService.listarPorPedido(idPedido));
        }

        // Si no es admin ni dueño del pedido, bloquear acceso
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tienes permisos para ver este pedido.");
    }

    /**
     * Lista todos los registros asociados a un producto concreto.
     * @param idProducto ID del producto.
     * @return Lista de PedidoProducto vinculados a ese producto.
     */
    @GetMapping("/por-producto/{idProducto}")
    public ResponseEntity<?> listarPorProducto(@PathVariable Integer idProducto) {
        // Verificar si el usuario autenticado es ADMIN
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para ver la relación de este producto con los pedidos.");
        }

        // Si es admin, retornar la relación del producto con los pedidos
        return ResponseEntity.ok(pedidoProductoService.listarPorProducto(idProducto));
    }
    /**
     * Obtiene la relación de un producto en un pedido específico.
     *
     * @param idPedido ID del pedido.
     * @param idProducto ID del producto.
     * @return Lista de registros de PedidoProducto encontrados.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> obtenerRelacionPedidoProducto(
            @RequestParam Integer idPedido,
            @RequestParam Integer idProducto) {

        // Obtener el usuario autenticado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        // Verificar si el usuario autenticado es ADMIN
        boolean esAdmin = esAdmin();

        // Obtener el pedido desde la base de datos
        Pedido pedido = pedidoService.obtenerPedidoPorId(idPedido);
        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado.");
        }

        // Verificar si el usuario autenticado es el dueño del pedido o es admin
        if (!esAdmin && !pedido.getUsuario().getUsername().equals(usernameAutenticado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para ver la relación de este pedido.");
        }

        // Obtener la relación del pedido con el producto
        List<PedidoProducto> relaciones = pedidoProductoService.obtenerRelacionPedidoProducto(idPedido, idProducto);
        return ResponseEntity.ok(relaciones);
    }



    private boolean esAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return false; // No autenticado o usuario anónimo
        }

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
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

}
