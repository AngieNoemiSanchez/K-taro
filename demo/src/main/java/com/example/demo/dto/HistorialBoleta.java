package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistorialBoleta(Long id, String codigo, LocalDateTime fecha, int productos,
                              String metodoPago, BigDecimal total) {}