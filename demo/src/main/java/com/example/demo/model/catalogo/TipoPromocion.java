package com.example.demo.model.catalogo;

public enum TipoPromocion {
    COMBO("Combo"),
    OFERTA("Oferta");

    private final String etiqueta;

    TipoPromocion(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}