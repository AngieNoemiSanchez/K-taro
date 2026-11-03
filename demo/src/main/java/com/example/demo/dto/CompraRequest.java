package com.example.demo.dto;

import java.util.List;

import com.example.demo.dto.cliente.MetodoDePago;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CompraRequest(
        @NotEmpty(message = "El carrito está vacío") @Valid List<Item> items,
        @Valid @NotNull(message = "Ingrese los datos de la tarjeta") MetodoDePago tarjeta) {

    public record Item(
            @NotBlank(message = "Producto inválido") String id,
            @Min(value = 1, message = "La cantidad mínima es 1")
            @Max(value = 99, message = "La cantidad máxima por producto es 99") int quantity) {
    }
}