package com.example.demo.controller.admin;

import com.example.demo.model.catalogo.Producto;
import com.example.demo.service.catalogo.ProductoService;
import com.example.demo.service.catalogo.PromocionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminCatalogController {

    private final ProductoService productoService;
    private final PromocionService promocionService;

    public AdminCatalogController(ProductoService productoService, PromocionService promocionService) {
        this.productoService = productoService;
        this.promocionService = promocionService;
    }

    @GetMapping("/catalogo")
    public String catalog(@RequestParam(defaultValue = "productos") String tipo, Model model) {
        String section = "promociones".equals(tipo) ? "promociones" : "productos";
        List<Producto> productos = productoService.listar();
        model.addAttribute("tipo", section);
        model.addAttribute("products", productos);
        model.addAttribute("promotions", promocionService.listar());
        model.addAttribute("productNames", productos.stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre)));
        return "catalogo/AdminCatalogo";
    }
}