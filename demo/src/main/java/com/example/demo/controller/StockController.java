package com.example.demo.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PromotionCard;
import com.example.demo.service.CustomerCatalogService;
import com.example.demo.service.catalogo.PromocionService;

@RestController
public class StockController {

    private final CustomerCatalogService catalog;
    private final PromocionService promociones;

    public StockController(CustomerCatalogService catalog, PromocionService promociones) {
        this.catalog = catalog;
        this.promociones = promociones;
    }

    @GetMapping("/api/stock")
    public Map<String, Integer> stock(@RequestParam List<String> ids) {
        Map<String, Integer> result = new LinkedHashMap<>();
        Map<String, Integer> promoStock = null;
        for (String id : ids) {
            if (id.startsWith("promo-")) {
                if (promoStock == null) {
                    promoStock = promociones.listarTarjetasVisibles().stream()
                            .collect(Collectors.toMap(PromotionCard::id, PromotionCard::stock));
                }
                result.put(id, promoStock.getOrDefault(id, 0));
            } else {
                result.put(id, catalog.availableUnits(id));
            }
        }
        return result;
    }
}