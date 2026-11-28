package com.example.demo.controller.cliente;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.cliente.MetodoDePago;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.pedido.TarjetaService;

@Controller
@RequestMapping("/cliente/perfil/tarjetas")
public class TarjetaPerfilController {

    private final TarjetaService tarjetaService;

    public TarjetaPerfilController(TarjetaService tarjetaService) {
        this.tarjetaService = tarjetaService;
    }

    @PostMapping
    public String agregar(@ModelAttribute("tarjetaForm") MetodoDePago datos,
                          Principal principal,
                          RedirectAttributes redirect) {
        try {
            tarjetaService.agregar(datos, principal.getName());
            redirect.addFlashAttribute("success", "Tarjeta guardada correctamente");
        } catch (BusinessException ex) {
            redirect.addFlashAttribute("errorTarjeta", ex.getMessage());
        }
        return "redirect:/cliente/perfil";
    }

    @PostMapping("/{id}/descartar")
    public String descartar(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
        try {
            tarjetaService.descartar(id, principal.getName());
            redirect.addFlashAttribute("success", "Tarjeta descartada");
        } catch (BusinessException ex) {
            redirect.addFlashAttribute("errorPerfil", ex.getMessage());
        }
        return "redirect:/cliente/perfil";
    }
}