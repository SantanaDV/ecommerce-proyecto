package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductoService productoService;

    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        List<Producto> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);

        // Verifica si el carrito existe en la sesión y lo pasa al modelo
        Object carrito = session.getAttribute("carrito");
        model.addAttribute("carrito", carrito);

        return "index";
    }
}
