package com.example.demo.Entity.pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.model.pedido.TipoItemCarrito;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito")
public class CarritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItemEntity> items = new ArrayList<>();

    @Column(name = "actualizado", nullable = false)
    private LocalDateTime actualizado = LocalDateTime.now();

    public CarritoEntity() {}

    public Optional<CarritoItemEntity> buscar(TipoItemCarrito tipo, Long referencia) {
        return items.stream()
                .filter(i -> i.getTipoItem() == tipo && i.getIdReferencia().equals(referencia))
                .findFirst();
    }

    public CarritoItemEntity agregarItem(TipoItemCarrito tipo, Long referencia) {
        CarritoItemEntity i = new CarritoItemEntity();
        i.setCarrito(this);
        i.setTipoItem(tipo);
        i.setIdReferencia(referencia);
        i.setCantidad(1);
        items.add(i);
        return i;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClienteEntity getCliente() { return cliente; }
    public void setCliente(ClienteEntity cliente) { this.cliente = cliente; }
    public List<CarritoItemEntity> getItems() { return items; }
    public LocalDateTime getActualizado() { return actualizado; }
    public void setActualizado(LocalDateTime actualizado) { this.actualizado = actualizado; }
}