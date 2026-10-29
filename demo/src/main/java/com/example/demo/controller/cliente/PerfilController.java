package com.example.demo.controller.cliente;

import com.example.demo.dto.PerfilForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.Entity.ClienteEntity;
import com.example.demo.mapper.ClienteMapper;
import com.example.demo.repository.ClienteRepository; // <-- Importamos el Repositorio
import com.example.demo.service.Cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/cliente/perfil")
public class PerfilController {

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public PerfilController(ClienteService clienteService, ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @GetMapping
    public String verPerfil(Principal principal, Model model) {
        ClienteEntity entity = clienteRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        model.addAttribute("cliente", clienteMapper.toModel(entity));
        model.addAttribute("perfilForm", aForm(entity));
        return "cliente/perfil";
    }

    @PostMapping
    public String guardarPerfil(@Valid @ModelAttribute("perfilForm") PerfilForm form,
                                BindingResult result,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirect) {
        ClienteEntity entity = clienteRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        if (!result.hasErrors()) {
            try {
                clienteService.actualizarDatos(entity.getId(), form);
                redirect.addFlashAttribute("success", "¡Perfil actualizado correctamente!");
                return "redirect:/cliente/perfil";
            } catch (BusinessException ex) {
                result.reject("perfil.error", ex.getMessage());
            }
        }
        model.addAttribute("cliente", clienteMapper.toModel(entity));
        return "cliente/perfil";
    }

    private PerfilForm aForm(ClienteEntity c) {
        PerfilForm f = new PerfilForm();
        f.setNombre(c.getNombre());
        f.setApellidos(c.getApellidos());
        f.setTelefono(c.getTelefono());
        f.setDireccion(c.getDireccion());
        return f;
    }
}