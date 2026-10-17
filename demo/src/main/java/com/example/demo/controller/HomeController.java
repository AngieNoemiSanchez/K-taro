package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ProductCatalogService;

@Controller
public class HomeController {

    private final ProductCatalogService productCatalog;

    public HomeController(ProductCatalogService productCatalog) {
        this.productCatalog = productCatalog;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productCatalog.getFeaturedProducts());
        model.addAttribute("promotionalProducts", productCatalog.getPromotionalProducts());
        return "inicio";
    }

    // Ventana 1: Busca en templates/carrito/carrito.html
    @GetMapping("/carrito")
    public String carrito() {
        return "carrito/carrito";
    }

    // Ventana 2: Busca en templates/pedidos/pago.html
    @GetMapping("/pago")
    public String pago() {
        return "pedidos/pago";
    }

    // Ventana 3: Busca en templates/pedidos/compra-exitosa.html
    @GetMapping("/compra-exitosa")
    public String compraExitosa() {
        return "pedidos/compra-exitosa";
    }
        // Vista Mi perfil
    @GetMapping("/perfil")
    public String perfil() {
        return "usuario/perfil";
    }

    // Vista Contáctanos
    @GetMapping("/contacto")
    public String contacto() {
        return "usuario/contacto";
    }
    // Vista de Promociones
@GetMapping("/promociones")
public String promociones() {
    return "promociones/promociones";
}
}