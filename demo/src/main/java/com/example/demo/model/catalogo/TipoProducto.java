package com.example.demo.model.catalogo;

import java.util.Arrays;
import java.util.Optional;

public enum TipoProducto {
    SNACK("Snacks", "snacks"),
    BEBIDA("Bebidas", "bebidas"),
    RAMEN("Ramen", "ramen"),
    OTROS("Otros", "otros");

    private final String etiqueta;
    private final String slug;

    TipoProducto(String etiqueta, String slug) {
        this.etiqueta = etiqueta;
        this.slug = slug;
    }

    public String getEtiqueta() { return etiqueta; }

    public String getSlug() { return slug; }

    public static Optional<TipoProducto> fromSlug(String slug) {
        return Arrays.stream(values())
                .filter(t -> t.slug.equalsIgnoreCase(slug))
                .findFirst();
    }
}