package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.dto.PedidoRequest;
import com.proyecto.ecommerce.dto.ProductoCantidadDTO;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.service.PedidoService;
import com.proyecto.ecommerce.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final ProductoService productoService;
    private final PedidoController pedidoController;

    public CarritoController(ProductoService productoService,
                             PedidoController pedidoController) {
        this.productoService = productoService;
        this.pedidoController = pedidoController;
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer idProducto,
                                   @RequestParam(defaultValue = "1") Integer cantidad,
                                   HttpSession session) {
        Producto producto = productoService.obtenerProductoPorId(idProducto);

        List<CarritoItem> carrito = obtenerCarrito(session);

        Optional<CarritoItem> itemExistente = carrito.stream()
                .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                .findFirst();

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + cantidad);
        } else {
            carrito.add(new CarritoItem(producto, cantidad));
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCarrito(@RequestParam Integer idProducto,
                                    @RequestParam Integer cantidad,
                                    HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);

        carrito.stream()
                .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .ifPresent(item -> item.setCantidad(cantidad));

        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Integer idProducto, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getProducto().getIdProducto().equals(idProducto));
        return "redirect:/carrito";
    }

    @PostMapping("/completar")
    public String completarPedido(HttpSession session, Authentication authentication) {
        List<CarritoItem> carrito = obtenerCarrito(session);

        // Validar stock antes de procesar
        boolean stockValido = carrito.stream()
                .allMatch(item -> item.getProducto().getStock() >= item.getCantidad());

        if (!stockValido) {
            return "redirect:/carrito?error=stock";
        }

        // Crear PedidoRequest
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setFecha(LocalDate.now());
        pedidoRequest.setEstado("PENDIENTE");

        List<ProductoCantidadDTO> productos = carrito.stream()
                .map(item -> new ProductoCantidadDTO(item.getProducto().getIdProducto(), item.getCantidad()))
                .collect(Collectors.toList());

        pedidoRequest.setProductos(productos);

        // Calcular total
        double total = carrito.stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();
        pedidoRequest.setTotal(total);

        // Crear pedido
        pedidoController.crearPedido(pedidoRequest);

        // Limpiar carrito
        session.removeAttribute("carrito");

        return "redirect:/pedidos/mios";
    }
    // Nuevo método para visualizar el carrito (Mi Carrito)
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        model.addAttribute("carrito", carrito);
        double total = carrito.stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();
        int totalItems = carrito.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
        model.addAttribute("total", total);
        model.addAttribute("totalItems", totalItems);
        return "carrito"; // Vista: templates/carrito.html
    }
    @ModelAttribute("total")
    public double calcularTotal(HttpSession session) {
        return obtenerCarrito(session).stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();
    }

    @ModelAttribute("totalItems")
    public int totalItems(HttpSession session) {
        return obtenerCarrito(session).stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    @ModelAttribute("pedidoValido")
    public boolean validarPedido(HttpSession session) {
        return obtenerCarrito(session).stream()
                .allMatch(item -> item.getCantidad() <= item.getProducto().getStock());
    }

    private List<CarritoItem> obtenerCarrito(HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    // Clase interna para manejar items del carrito
    private static class CarritoItem {
        private Producto producto;
        private Integer cantidad;

        public CarritoItem(Producto producto, Integer cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public void setProducto(Producto producto) {
            this.producto = producto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}