package com.example.demo.dto.admin;

public record PedidoAdminFila(Long id, String codigo, String cliente, String fecha,
                              String estado, String etiqueta,
                              String siguiente, String siguienteEtiqueta) {}