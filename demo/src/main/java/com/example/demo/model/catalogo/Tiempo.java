package com.example.demo.model.catalogo;

public enum Tiempo {
    siempre("Siempre disponible"),
    lunes("Solo lunes"),
    domingos("Solo domingos"),
    miercoles("Solo miércoles");

    private final String etiqueta;

    Tiempo(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}