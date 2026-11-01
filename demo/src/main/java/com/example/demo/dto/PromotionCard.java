package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public record PromotionCard(
        String id,
        String name,
        String description,
        String image,
        BigDecimal price,
        BigDecimal regularPrice,
        String durationLabel,
        List<String> productNames,
        int stock,
        boolean availableToday,
        String availabilityNote) {

    public boolean inStock() {
        return stock > 0;
    }

    public boolean canBuy() {
        return inStock() && availableToday;
    }

    public int availableUnits() {
        return availableToday ? stock : 0;
    }

    public String buttonLabel() {
        if (!inStock()) {
            return "Agotado";
        }
        if (!availableToday) {
            return availabilityNote;
        }
        return "Agregar al carrito";
    }
}