package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.ProductAdminService;
import com.example.demo.service.PromotionService;

@Controller
@RequestMapping("/admin")
public class AdminCatalogController {

    private final ProductAdminService productAdmin;
    private final PromotionService promotions;

    public AdminCatalogController(ProductAdminService productAdmin, PromotionService promotions) {
        this.productAdmin = productAdmin;
        this.promotions = promotions;
    }

    @GetMapping("/catalogo")
    public String catalog(@RequestParam(defaultValue = "productos") String tipo, Model model) {
        String section = "promociones".equals(tipo) ? "promociones" : "productos";
        model.addAttribute("tipo", section);
        model.addAttribute("products", productAdmin.findAll());
        model.addAttribute("promotions", promotions.findAll());
        model.addAttribute("productNames", productAdmin.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.example.demo.model.AdminProduct::id, com.example.demo.model.AdminProduct::name)));
        return "productos/AdminCatalogo";
    }

    @PostMapping("/productos/{id}/stock")
    public String increaseStock(@PathVariable String id, @RequestParam(defaultValue = "1") Integer cantidad,
                                RedirectAttributes redirect) {
        if (cantidad == null || cantidad < 1 || cantidad > 100000) {
            redirect.addFlashAttribute("error", "La cantidad a agregar debe ser un número entero mayor a 0.");
        } else if (productAdmin.addStock(id, cantidad).isPresent()) {
            redirect.addFlashAttribute("mensaje", "Se agregaron " + cantidad + " unidades al stock.");
        } else {
            redirect.addFlashAttribute("error", "No se encontró el producto.");
        }
        return "redirect:/admin/catalogo?tipo=productos";
    }
}