package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    @PostMapping("/registro")
    public String processRegister(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("success", "¡Cuenta creada exitosamente! Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }
}