package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

@GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String showRegisterForm() {
        return "auth/registro";
    }
}