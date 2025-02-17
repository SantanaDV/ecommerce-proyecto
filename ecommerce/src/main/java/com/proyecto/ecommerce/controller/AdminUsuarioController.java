package com.proyecto.ecommerce.controller;

import com.proyecto.ecommerce.entity.Usuario;
import com.proyecto.ecommerce.service.UsuarioService;
import com.proyecto.ecommerce.validation.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios"; // Vista: src/main/resources/templates/admin/usuarios.html
    }

    @GetMapping("/admin/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        // Agregamos el usuario al modelo con el nombre "usuarioUpdate"
        model.addAttribute("usuarioUpdate", usuario);
        return "admin/form-usuario"; // La vista en templates/admin/form-usuario.html
    }

    @PostMapping("/admin/usuarios/actualizar")
    public String actualizarUsuario(
            @Valid @ModelAttribute("usuarioUpdate") Usuario usuario,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "admin/form-usuario";
        }
        usuarioService.actualizarUsuario(usuario.getIdUsuario(), usuario);
        return "redirect:/admin/usuarios";
    }
    @PostMapping("/admin/usuarios/delete/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/admin/usuarios";
    }
}
