package com.example.demo.Entity.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.model.pedido.EstadoPedido;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.REGISTRADO;

    @Column(name = "tarjeta", length = 30)
    private String tarjeta;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItemEntity> items = new ArrayList<>();

    public PedidoEntity() {}

    public void agregarItem(String nombre, int cantidad, BigDecimal precioUnitario) {
        PedidoItemEntity i = new PedidoItemEntity();
        i.setPedido(this);
        i.setNombre(nombre);
        i.setCantidad(cantidad);
        i.setPrecioUnitario(precioUnitario);
        items.add(i);
    }

    public Long getId() { return id; }
    public ClienteEntity getCliente() { return cliente; }
    public void setCliente(ClienteEntity cliente) { this.cliente = cliente; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public String getTarjeta() { return tarjeta; }
    public void setTarjeta(String tarjeta) { this.tarjeta = tarjeta; }
    public List<PedidoItemEntity> getItems() { return items; }
}