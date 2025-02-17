package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Pedido;
import com.proyecto.ecommerce.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PedidoWebController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pedidos/mios")
    public String misPedidos(Model model, Authentication authentication) {
        // Se obtiene el username del usuario autenticado
        String username = authentication.getName();
        // Se listan sólo los pedidos de ese usuario
        List<Pedido> pedidos = pedidoService.listarPedidosPorUsername(username);
        model.addAttribute("pedidos", pedidos);
        return "mis-pedidos"; // Vista: src/main/resources/templates/mis-pedidos.html
    }
}