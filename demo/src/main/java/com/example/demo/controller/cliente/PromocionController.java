package com.example.demo.controller.cliente;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.catalogo.PromocionService;

@Controller
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping("/promociones")
    public String promociones(@RequestParam(defaultValue = "combos") String tipo, Model model) {
        boolean ofertas = "ofertas".equals(tipo);
        model.addAttribute("tipo", ofertas ? "ofertas" : "combos");
        if (ofertas) {
            model.addAttribute("offers", promocionService.listarOfertasVisibles());
        } else {
            model.addAttribute("promotions", promocionService.listarTarjetasVisibles());
        }
        return "promociones/promociones";
    }
}