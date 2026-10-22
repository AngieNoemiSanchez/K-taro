package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // 1. Redirige http://localhost:8081/admin a http://localhost:8081/admin/login
    @GetMapping({"", "/"})
    public String redirectToLogin() {
        return "redirect:/admin/login";
    }

    // 2. Muestra la vista del formulario de login
    @GetMapping("/login")
    public String showAdminLoginForm() {
        return "admin/login";
    }

    // 3. Procesa el inicio de sesión aceptando 'admin' o 'admin@ktaro.com'
    @PostMapping("/login")
    public String processAdminLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        // Permite ingresar con usuario "admin" O correo "admin@ktaro.com"
        boolean isValidUser = "admin".equalsIgnoreCase(username.trim()) || "admin@ktaro.com".equalsIgnoreCase(username.trim());
        boolean isValidPassword = "admin123".equals(password);

        if (isValidUser && isValidPassword) {
            return "redirect:/admin/dashboard"; // Redirige al panel de administración
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario o contraseña incorrectos.");
            return "redirect:/admin/login";
        }
    }
}