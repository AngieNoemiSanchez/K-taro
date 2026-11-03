package com.example.demo.model.pedido;

public enum EstadoTarjeta {
    ACTIVA("Activa"),
    DESCARTADA("Descartada");

    private final String etiqueta;

    EstadoTarjeta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}