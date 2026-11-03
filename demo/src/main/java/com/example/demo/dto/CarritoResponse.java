package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarritoResponse(
        List<CarritoItemDto> items,
        BigDecimal total) {
}