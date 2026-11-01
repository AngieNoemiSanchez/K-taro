package com.example.demo.dto;

import java.math.BigDecimal;

public record OfferCard(
        String id,
        Long productId,
        String name,
        String image,
        BigDecimal regularPrice,
        BigDecimal offerPrice,
        int discountPercent,
        String durationLabel,
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