package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;

public record AdminProduct(
        String id,
        String name,
        String description,
        String category,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer discountPercent,
        Integer stock,
        boolean active,
        String image,
        List<String> relatedProductIds) {
}