package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;

public record Promotion(
        String id,
        String name,
        String duration,
        String description,
        List<String> productIds,
        BigDecimal price) {
}