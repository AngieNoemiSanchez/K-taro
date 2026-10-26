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
        int stock) {

    public boolean inStock() {
        return stock > 0;
    }
}