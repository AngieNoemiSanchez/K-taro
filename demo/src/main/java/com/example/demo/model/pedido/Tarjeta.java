package com.example.demo.model.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Tarjeta {
    private final Long id;
    private final String numeroTarjeta;
    private final String cvvHash;
    private final String titular;
    private final LocalDateTime fechaVencimiento;
    private final BigDecimal saldo;
    private final EstadoTarjeta estado;

    public Tarjeta(Long id, String numeroTarjeta, String cvvHash, String titular,
                   LocalDateTime fechaVencimiento, BigDecimal saldo, EstadoTarjeta estado) {
        this.id = id;
        this.numeroTarjeta = numeroTarjeta;
        this.cvvHash = cvvHash;
        this.titular = titular;
        this.fechaVencimiento = fechaVencimiento;
        this.saldo = saldo;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public String getCvvHash() {
        return cvvHash;
    }

    public String getTitular() {
        return titular;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public EstadoTarjeta getEstado() {
        return estado;
    }

    public boolean isActiva() {
        return estado == EstadoTarjeta.ACTIVA;
    }

    public boolean estaVencida(LocalDateTime ahora) {
        return ahora.isAfter(fechaVencimiento);
    }

    public String getUltimosDigitos() {
        return numeroTarjeta.substring(Math.max(0, numeroTarjeta.length() - 4));
    }

    public String getNumeroEnmascarado() {
        return "**** **** **** " + getUltimosDigitos();
    }
}