package com.example.demo.model;

import java.math.BigDecimal;

public record AdminProduct(
        String id,
        String name,
        String description,
        String category,
        BigDecimal price,
        Integer stock,
        boolean active,
        String image) {
}