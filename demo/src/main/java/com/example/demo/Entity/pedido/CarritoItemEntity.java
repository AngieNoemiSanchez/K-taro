package com.example.demo.Entity.pedido;

import com.example.demo.model.pedido.TipoItemCarrito;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_carrito", "tipo_item", "id_referencia"}))
public class CarritoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrito", nullable = false)
    private CarritoEntity carrito;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 20)
    private TipoItemCarrito tipoItem;

    @Column(name = "id_referencia", nullable = false)
    private Long idReferencia;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public CarritoItemEntity() {}

    public String getCartId() {
        return switch (tipoItem) {
            case PRODUCTO -> String.valueOf(idReferencia);
            case COMBO -> "promo-" + idReferencia;
            case OFERTA -> "oferta-" + idReferencia;
        };
    }

    public Long getId() { return id; }
    public CarritoEntity getCarrito() { return carrito; }
    public void setCarrito(CarritoEntity carrito) { this.carrito = carrito; }
    public TipoItemCarrito getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItemCarrito tipoItem) { this.tipoItem = tipoItem; }
    public Long getIdReferencia() { return idReferencia; }
    public void setIdReferencia(Long idReferencia) { this.idReferencia = idReferencia; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}