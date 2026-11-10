package com.example.demo.dto;

import java.math.BigDecimal;

public record CompraResponse(BigDecimal total, String tarjeta, Long pedidoId) {
}