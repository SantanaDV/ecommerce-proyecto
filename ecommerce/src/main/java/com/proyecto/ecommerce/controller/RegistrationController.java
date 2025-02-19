package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.exception.CustomException;
import com.proyecto.ecommerce.security.TokenUtil;
import com.proyecto.ecommerce.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";  // Vista: src/main/resources/templates/registro.html
    }

    @PostMapping("/registro")
    public String registerUser(@Valid @ModelAttribute("usuario") Usuario usuario,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "registro";
        }
        try {
            // Se crea el usuario
            Usuario newUser = usuarioService.crearUsuario(usuario);
            System.out.println("Usuario registrado: " + newUser);
        } catch (CustomException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registro";
        }
        // Redirige al login para que el usuario inicie sesión
        return "redirect:/login-page";
    }
}
