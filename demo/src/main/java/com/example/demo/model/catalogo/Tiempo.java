package com.example.demo.model.catalogo;

import java.time.DayOfWeek;

public enum Tiempo {
    siempre("Siempre disponible", null, null),
    domingos("Solo domingos", DayOfWeek.SUNDAY, "Disponible solo los domingos"),
    miercoles("Solo miércoles", DayOfWeek.WEDNESDAY, "Disponible solo los miércoles");

    private final String etiqueta;
    private final DayOfWeek dia;
    private final String aviso;

    Tiempo(String etiqueta, DayOfWeek dia, String aviso) {
        this.etiqueta = etiqueta;
        this.dia = dia;
        this.aviso = aviso;
    }

    public String getEtiqueta() { return etiqueta; }

    public String getAviso() { return aviso; }

    public boolean disponibleEn(DayOfWeek hoy) {
        return dia == null || dia == hoy;
    }
}