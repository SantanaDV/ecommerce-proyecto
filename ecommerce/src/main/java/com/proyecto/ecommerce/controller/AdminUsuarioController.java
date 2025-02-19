package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.dto.PedidoRequest;
import com.proyecto.ecommerce.dto.ProductoCantidadDTO;
import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.service.PedidoService;
import com.proyecto.ecommerce.service.ProductoService;
import com.proyecto.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;
    @Autowired
    private PedidoService pedidoService;

    // Gestión de Usuarios

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios"; // Vista: src/main/resources/templates/admin/usuarios.html
    }

    // Muestra el formulario para crear un nuevo usuario
    // Método para mostrar el formulario de creación de un nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        // Creamos un objeto Usuario vacío
        Usuario usuario = new Usuario();
        // Lo agregamos al modelo con el nombre "usuarioUpdate" para que el formulario lo use
        model.addAttribute("usuarioUpdate", usuario);
        // Definimos la URL de acción para crear (en vez de actualizar)
        model.addAttribute("actionUrl", "/admin/usuarios/crear");
        return "admin/form-usuario"; // Vista: src/main/resources/templates/admin/form-usuario.html
    }

    // Método para procesar la creación de un nuevo usuario
    @PostMapping("/usuarios/crear")
    public String crearUsuario(
            @Valid @ModelAttribute("usuarioUpdate") Usuario usuario,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            // En caso de error, volvemos al formulario
            return "admin/form-usuario";
        }
        usuarioService.crearUsuario(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        // Agregamos el objeto al modelo con el nombre "usuarioUpdate"
        model.addAttribute("usuarioUpdate", usuario);
        // También se puede agregar la URL de acción, esto es porque antes en el mismo formulario hacia las dos cosas, posteriormente cambiado
        model.addAttribute("actionUrl", (usuario.getIdUsuario() == null) ? "/admin/usuarios/crear" : "/admin/usuarios/actualizar");
        return "admin/form-usuario"; // Vista: src/main/resources/templates/admin/form-usuario.html
    }

    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(
            @Valid @ModelAttribute("usuarioUpdate") Usuario usuario,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            // Si hay errores de validación, vuelve al formulario.
            return "admin/form-usuario";
        }
        usuarioService.actualizarUsuario(usuario.getIdUsuario(), usuario);
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/delete/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/admin/usuarios";
    }

    // Gestión de Productos

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);
        return "admin/productos";  // Vista: src/main/resources/templates/admin/productos.html
    }

    // Formulario para crear un nuevo producto
    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("actionUrl", "/admin/productos/crear");
        return "admin/form-producto";  // Vista: src/main/resources/templates/admin/form-producto.html
    }

    // Procesa la creación de un producto
    @PostMapping("/productos/crear")
    public String crearProducto(@Valid @ModelAttribute("producto") Producto producto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "admin/form-producto";
        }
        productoService.crearProducto(producto);
        return "redirect:/admin/productos";
    }

    // Formulario para editar un producto existente
    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable("id") Integer id, Model model) {
        Producto producto = productoService.obtenerProductoPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("actionUrl", "/admin/productos/actualizar");
        return "admin/form-producto";  // Se utiliza el mismo formulario para creación y edición
    }

    // Procesa la actualización de un producto
    @PostMapping("/productos/actualizar")
    public String actualizarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            return "admin/form-producto";
        }
        productoService.actualizarProducto(producto.getIdProducto(), producto);
        return "redirect:/admin/productos";
    }

    // Elimina un producto
    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        productoService.eliminarProducto(id);
        return "redirect:/admin/productos";
    }

    // Lista todos los pedidos
    @GetMapping("/pedidos")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        return "admin/pedidos";  // Vista: src/main/resources/templates/admin/pedidos.html
    }

    /**
     * Muestra el formulario para crear un nuevo pedido por parte del administrador.
     * En este formulario se muestra la lista de productos disponibles y se permite
     * ingresar la cantidad deseada para cada uno.
     */
    @GetMapping("/pedidos/nuevo")
    public String nuevoPedidoAdmin(Model model) {
        // Obtenemos la lista de productos disponibles
        List<Producto> productos = productoService.listarProductos();
        List<Usuario> usuarios = usuarioService.listarUsuarios(); // Agregamos la lista de usuarios

        // Creamos un PedidoRequest y, por cada producto, inicializamos un ProductoCantidadDTO con cantidad 0.
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setFecha(LocalDate.now());
        pedidoRequest.setEstado("PENDIENTE");
        pedidoRequest.setProductos(
                productos.stream()
                        .map(prod -> new ProductoCantidadDTO(prod.getIdProducto(), 0))
                        .collect(Collectors.toList())
        );

        // Creamos un mapa de productos, indexado por su id
        Map<Integer, Producto> productosMap = productos.stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Function.identity()));

        // Agregamos el PedidoRequest y la lista de productos al modelo para poder mostrarlos en la vista.
        model.addAttribute("pedidoRequest", pedidoRequest);
        model.addAttribute("productos", productos);
        model.addAttribute("usuarios", usuarios); // ¡Importante para que el modelo cargue los usuarios!
        model.addAttribute("productosMap", productosMap); // ¡Agregado el mapa de productos al modelo!
        model.addAttribute("actionUrl", "/admin/pedidos/crear-admin");
        return "admin/crear-pedido"; // Vista: templates/admin/crear-pedido.html
    }

    /**
     * Procesa el formulario de creación de pedido realizado por el administrador.
     * Se filtran los productos con cantidad > 0, se valida que exista stock suficiente,
     * se calcula el total, se actualizan los stocks y se crea la relación PedidoProducto.
     */
    @PostMapping("/pedidos/crear-admin")
    public String crearPedidoAdmin(@ModelAttribute("pedidoRequest") PedidoRequest pedidoRequest,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.listarProductos());
            return "admin/crear-pedido";
        }

        // Filtrar solo los productos con cantidad > 0
        List<ProductoCantidadDTO> productosSolicitados = pedidoRequest.getProductos().stream()
                .filter(pcd -> pcd.getCantidad() != null && pcd.getCantidad() > 0)
                .collect(Collectors.toList());
        if (productosSolicitados.isEmpty()) {
            model.addAttribute("error", "Debes seleccionar al menos un producto con cantidad mayor que 0.");
            model.addAttribute("productos", productoService.listarProductos());
            return "admin/crear-pedido";
        }
        pedidoRequest.setProductos(productosSolicitados);

        // Validar stock y calcular total
        double total = 0;
        for (ProductoCantidadDTO pcd : productosSolicitados) {
            Producto producto = productoService.obtenerProductoPorId(pcd.getIdProducto());
            if (producto.getStock() < pcd.getCantidad()) {
                model.addAttribute("error", "No hay suficiente stock para el producto: " + producto.getNombre());
                model.addAttribute("productos", productoService.listarProductos());
                return "admin/crear-pedido";
            }
            total += producto.getPrecio() * pcd.getCantidad();
        }
        pedidoRequest.setTotal(total);

        Usuario usuario = usuarioService.obtenerUsuarioPorId(pedidoRequest.getUsuarioId());

        // Obtenemos al admin autenticado (quien crea el pedido)
        String usernameAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioService.obtenerUsuarioPorUsername(usernameAdmin);

        // Llamamos al método de servicio que crea el pedido, actualiza el stock y guarda las relaciones
        pedidoService.crearPedidoAdmin(pedidoRequest, admin);

        return "redirect:/admin/pedidos";
    }


    // Procesa la creación de un nuevo pedido
    @PostMapping("/pedidos/crear")
    public String crearPedido(@Valid @ModelAttribute("pedido") Pedido pedido,
                              BindingResult result,
                              @RequestParam("usuarioId") Integer usuarioId,
                              Model model) {
        if (result.hasErrors()) {
            // En caso de error, volvemos a cargar la lista de usuarios para el formulario
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
            return "admin/form-pedido";
        }
        // Obtenemos el usuario seleccionado y lo asignamos al pedido
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        pedido.setUsuario(usuario);
        pedidoService.crearPedido(pedido);
        return "redirect:/admin/pedidos";
    }

    // Muestra el formulario para editar un pedido existente
    @GetMapping("/pedidos/editar/{id}")
    public String editarPedido(@PathVariable("id") Integer id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        model.addAttribute("pedido", pedido);
        model.addAttribute("actionUrl", "/admin/pedidos/actualizar"); // URL para actualización
        return "admin/form-pedido";  // Vista: templates/admin/form-pedido.html
    }


    // Procesa la actualización de un pedido
    @PostMapping("/pedidos/actualizar")
    public String actualizarPedido(@Valid @ModelAttribute("pedido") Pedido pedido,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            return "admin/form-pedido";
        }
        // En edición, el usuario no se modifica
        pedidoService.actualizarPedido(pedido.getIdPedido(), pedido);
        return "redirect:/admin/pedidos";
    }

    // Elimina un pedido
    @PostMapping("/pedidos/eliminar/{id}")
    public String eliminarPedido(@PathVariable("id") Integer id) {
        pedidoService.eliminarPedido(id);
        return "redirect:/admin/pedidos";
    }





}
