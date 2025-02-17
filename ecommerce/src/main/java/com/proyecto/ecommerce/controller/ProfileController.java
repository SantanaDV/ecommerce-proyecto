package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        // El objeto Authentication contiene información del usuario autenticado.
        model.addAttribute("usuario", authentication.getPrincipal());
        return "profile"; // Retorna templates/profile.html
    }

    @GetMapping("/perfil/datos")
    public String misDatos(Model model, Authentication authentication) {
        String username = authentication.getName();
        // Consultamos la información completa del usuario desde la base de datos
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
        model.addAttribute("usuario", usuario);
        return "perfil-datos"; // Retorna la vista: src/main/resources/templates/perfil-datos.html
    }
}
