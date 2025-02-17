package com.proyecto.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login-page")
    public String loginPage() {
        // Retorna la vista login.html
        return "login";
    }
}