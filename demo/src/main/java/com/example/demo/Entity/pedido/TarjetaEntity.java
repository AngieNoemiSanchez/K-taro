package com.example.demo.Entity.pedido;

import java.math.BigDecimal;

import com.example.demo.model.pedido.EstadoTarjeta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarjeta")
public class TarjetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 16)
    private String numero;

    @Column(name = "titular", nullable = false, length = 60)
    private String titular;

    @Column(name = "vencimiento", nullable = false, length = 5)
    private String vencimiento;            // MM/AA

    @Column(name = "cvv_hash", nullable = false, length = 60)
    private String cvvHash;                // BCrypt

    @Column(name = "saldo", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoTarjeta estado = EstadoTarjeta.ACTIVA;

    public TarjetaEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    public String getVencimiento() { return vencimiento; }
    public void setVencimiento(String vencimiento) { this.vencimiento = vencimiento; }
    public String getCvvHash() { return cvvHash; }
    public void setCvvHash(String cvvHash) { this.cvvHash = cvvHash; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public EstadoTarjeta getEstado() { return estado; }
    public void setEstado(EstadoTarjeta estado) { this.estado = estado; }
}