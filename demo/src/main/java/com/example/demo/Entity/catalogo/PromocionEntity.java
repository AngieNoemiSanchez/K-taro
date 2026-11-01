package com.example.demo.Entity.catalogo;

import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Tiempo;
import com.example.demo.model.catalogo.TipoPromocion;
import org.hibernate.annotations.ColumnDefault;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "promocion")
public class PromocionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tiempo", nullable = false, length = 20)
    private Tiempo tiempo = Tiempo.siempre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.ACTIVO;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @ManyToMany
    @JoinTable(name = "promocion_producto",
            joinColumns = @JoinColumn(name = "id_promocion"),
            inverseJoinColumns = @JoinColumn(name = "id_producto"))
    private Set<ProductoEntity> productos = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'COMBO'")
    @Column(name = "tipo_promocion", nullable = false, length = 20)
    private TipoPromocion tipoPromocion = TipoPromocion.COMBO;

    public PromocionEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Tiempo getTiempo() { return tiempo; }
    public void setTiempo(Tiempo tiempo) { this.tiempo = tiempo; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Set<ProductoEntity> getProductos() { return productos; }
    public void setProductos(Set<ProductoEntity> productos) { this.productos = productos; }
    public TipoPromocion getTipoPromocion() { return tipoPromocion; }
    public void setTipoPromocion(TipoPromocion tipoPromocion) { this.tipoPromocion = tipoPromocion; }
}