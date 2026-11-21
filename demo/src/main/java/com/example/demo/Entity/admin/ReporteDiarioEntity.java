package com.example.demo.Entity.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "reporte_diario", uniqueConstraints = @UniqueConstraint(columnNames = "fecha"))
public class ReporteDiarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "pedidos_recibidos", nullable = false)
    private int pedidosRecibidos;

    @Column(name = "pedidos_completados", nullable = false)
    private int pedidosCompletados;

    @Column(name = "unidades", nullable = false)
    private int unidades;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;

    public ReporteDiarioEntity() {}

    public ReporteDiarioEntity(LocalDate fecha, int pedidosRecibidos, int pedidosCompletados,
                               int unidades, BigDecimal monto) {
        this.fecha = fecha;
        this.pedidosRecibidos = pedidosRecibidos;
        this.pedidosCompletados = pedidosCompletados;
        this.unidades = unidades;
        this.monto = monto;
    }

    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public int getPedidosRecibidos() { return pedidosRecibidos; }
    public int getPedidosCompletados() { return pedidosCompletados; }
    public int getUnidades() { return unidades; }
    public BigDecimal getMonto() { return monto; }
}