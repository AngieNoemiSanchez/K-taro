package com.example.demo.controller.cliente;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.CustomerCatalogService;
import com.example.demo.service.catalogo.PromocionService;

@Controller
public class HomeController {

    private final CustomerCatalogService productCatalog;
    private final PromocionService promocionService;

    public HomeController(CustomerCatalogService productCatalog, PromocionService promocionService) {
        this.productCatalog = productCatalog;
        this.promocionService = promocionService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productCatalog.getFeaturedProducts());
        model.addAttribute("offers", promocionService.listarOfertasVisibles().stream().limit(4).toList());
        return "cliente/inicio";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "usuario/contacto";
    }

    /** Enlaces viejos a /perfil llevan al perfil real del cliente. */
    @GetMapping("/perfil")
    public String perfil() {
        return "redirect:/cliente/perfil";
    }
}