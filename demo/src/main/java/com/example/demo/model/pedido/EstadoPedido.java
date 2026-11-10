package com.example.demo.model.pedido;

public enum EstadoPedido {
    REGISTRADO("Registrado"),
    EN_PREPARACION("En preparación"),
    TERMINADO("Terminado"),
    RECOGIDO("Recogido");

    private final String etiqueta;

    EstadoPedido(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    public EstadoPedido siguiente() {
        EstadoPedido[] todos = values();
        return ordinal() + 1 < todos.length ? todos[ordinal() + 1] : null;
    }
}