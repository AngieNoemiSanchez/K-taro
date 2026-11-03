package com.example.demo.dto;

import java.math.BigDecimal;

public record CarritoItemDto(
        String id,
        String name,
        BigDecimal price,
        int quantity,
        String category) {
}