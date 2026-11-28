package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BoletaDetalle(String codigo, String cliente, String dni, String correo,
                            LocalDateTime fecha, String metodoPago,
                            List<Item> items, BigDecimal total) {

    public record Item(String nombre, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}
}