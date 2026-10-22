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

    @GetMapping({ "", "/" })
    public String redirectToLogin() {
        return "redirect:/admin/login";
    }

    @GetMapping("/login")
    public String showAdminLoginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String processAdminLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        boolean isValidUser = "admin".equalsIgnoreCase(username.trim())
                || "admin@ktaro.com".equalsIgnoreCase(username.trim());
        boolean isValidPassword = "admin123".equals(password);

        if (isValidUser && isValidPassword) {
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario o contraseña incorrectos.");
            return "redirect:/admin/login";
        }
    }

    // Nueva ruta para el Dashboard
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/catalogo")
    public String catalogo() {
        return "admin/catalogo"; // Buscará templates/admin/catalogo.html
    }
}