package com.example.demo.controller;

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
        model.addAttribute("promotions", promocionService.listarTarjetasVisibles().stream().limit(4).toList());
        return "cliente/inicio";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito/carrito";
    }

    @GetMapping("/pago")
    public String pago() {
        return "pedidos/pago";
    }

    @GetMapping("/compra-exitosa")
    public String compraExitosa() {
        return "pedidos/compra-exitosa";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "usuario/perfil";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "usuario/contacto";
    }

    // Promociones creadas en el admin
    @GetMapping("/promociones")
    public String promociones(Model model) {
        model.addAttribute("promotions", promocionService.listarTarjetasVisibles());
        return "promociones/promociones";
    }
}