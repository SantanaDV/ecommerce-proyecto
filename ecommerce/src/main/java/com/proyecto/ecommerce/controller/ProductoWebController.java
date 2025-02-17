package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Producto;
import com.proyecto.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ProductoWebController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/productos")
    public String listarProductosWeb(Model model) {
        List<Producto> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);
        return "productos"; // Se renderiza la plantilla: src/main/resources/templates/productos.html
    }
}
