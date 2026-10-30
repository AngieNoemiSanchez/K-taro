package com.example.demo.model.catalogo;

public enum TipoProducto {
    SNACK("Snacks"), BEBIDA("Bebidas"), RAMEN("Ramen"), OTROS("Otros");

    private final String etiqueta;
    TipoProducto(String etiqueta) { this.etiqueta = etiqueta; }
    public String getEtiqueta() { return etiqueta; }
}