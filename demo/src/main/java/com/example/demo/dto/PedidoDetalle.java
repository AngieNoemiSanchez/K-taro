package com.example.demo.dto;

import java.math.BigDecimal;

public record PedidoDetalle(Long id, String codigo, String usuario, String correo,
                            String metodoPago, BigDecimal total,
                            String estadoEtiqueta, int paso) {}