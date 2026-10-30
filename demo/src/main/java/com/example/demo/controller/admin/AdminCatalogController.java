package com.example.demo.controller.admin;

import com.example.demo.model.AdminProduct;
import com.example.demo.service.PromotionService;
import com.example.demo.service.catalogo.ProductAdminService;
import com.example.demo.service.catalogo.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminCatalogController {

    private final ProductoService productoService;
    private final ProductAdminService productAdmin;
    private final PromotionService promotions;

    public AdminCatalogController(ProductoService productoService,
                                  ProductAdminService productAdmin,
                                  PromotionService promotions) {
        this.productoService = productoService;
        this.productAdmin = productAdmin;
        this.promotions = promotions;
    }

    @GetMapping("/catalogo")
    public String catalog(@RequestParam(defaultValue = "productos") String tipo, Model model) {
        String section = "promociones".equals(tipo) ? "promociones" : "productos";
        model.addAttribute("tipo", section);
        model.addAttribute("products", productoService.listar());
        model.addAttribute("promotions", promotions.findAll());
        model.addAttribute("productNames", productAdmin.findAll().stream()
                .collect(Collectors.toMap(AdminProduct::id, AdminProduct::name)));
        return "catalogo/AdminCatalogo";
    }
}