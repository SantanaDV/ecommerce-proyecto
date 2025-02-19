package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.dto.PedidoRequest;
import com.proyecto.ecommerce.dto.ProductoCantidadDTO;
import com.proyecto.ecommerce.entity.CarritoItem;
import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(idProducto);
        List<CarritoItem> carrito = obtenerCarrito(session);

        // Calcular la cantidad total deseada
        int cantidadActual = carrito.stream()
                .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                .mapToInt(CarritoItem::getCantidad)
                .sum();
        int totalDeseado = cantidadActual + cantidad;

        // Verificar si hay suficiente stock
        if (producto.getStock() < totalDeseado) {
            redirectAttributes.addFlashAttribute("error", "No hay suficiente stock disponible para este producto.");
            return "redirect:/carrito";
        }

        // Si ya existe el producto en el carrito, se actualiza la cantidad
        Optional<CarritoItem> itemExistente = carrito.stream()
                .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                .findFirst();
        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(totalDeseado);
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
    public String completarPedido(HttpSession session, Authentication authentication, RedirectAttributes redirectAttributes) {
        List<CarritoItem> carrito = obtenerCarrito(session);

        // Validar stock antes de procesar:
        for (CarritoItem item : carrito) {
            if (item.getCantidad() > item.getProducto().getStock()) {
                redirectAttributes.addFlashAttribute("error", "No hay suficiente stock para el producto: "
                        + item.getProducto().getNombre());
                return "redirect:/carrito";
            }
        }

        // Crear PedidoRequest
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setFecha(LocalDate.now());
        pedidoRequest.setEstado("PENDIENTE");

        List<ProductoCantidadDTO> productos = new ArrayList<>();
        double total = 0;
        // Recorrer cada item para armar la lista y actualizar el stock
        for (CarritoItem item : carrito) {
            int cantidad = item.getCantidad();
            Producto producto = item.getProducto();

            // Acumular el total
            total += producto.getPrecio() * cantidad;
            // Agregar al DTO la cantidad solicitada
            productos.add(new ProductoCantidadDTO(producto.getIdProducto(), cantidad));

            // Actualizar el stock del producto
            producto.setStock(producto.getStock() - cantidad);
            productoService.actualizarProducto(producto.getIdProducto(), producto);
        }
        pedidoRequest.setProductos(productos);
        pedidoRequest.setTotal(total);

        // Crear pedido
        pedidoController.crearPedido(pedidoRequest);

        // Limpiar el carrito
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



}