package com.example.demo.controller.admin;

import com.example.demo.Entity.admin.AdministradorEntity;
import com.example.demo.mapper.AdministradorMapper;
import com.example.demo.model.admin.Administrador;
import com.example.demo.repository.AdministradorRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminProfileController {

    private final AdministradorRepository repo;
    private final AdministradorMapper mapper;

    public AdminProfileController(AdministradorRepository repo, AdministradorMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping("/perfil")
    public String verPerfil(Principal principal, Model model) {
        AdministradorEntity entity = repo.findByCorreoIgnoreCase(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado"));
        Administrador admin = mapper.toModel(entity);

        String iniciales = (inicial(admin.getNombre()) + inicial(admin.getApellido())).toUpperCase();

        model.addAttribute("admin", admin);
        model.addAttribute("iniciales", iniciales);
        model.addAttribute("codigoAdmin", String.format("ADM-%02d", admin.getIdAdmin()));
        return "admin/perfil";
    }

    private String inicial(String s) {
        return (s == null || s.isBlank()) ? "" : s.trim().substring(0, 1);
    }
}