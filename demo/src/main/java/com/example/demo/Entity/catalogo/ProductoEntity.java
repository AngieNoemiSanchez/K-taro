package com.example.demo.Entity.catalogo;

import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.TipoProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 500)
    @NotBlank
    @Size(max = 500)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 8, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    @NotNull
    @Min(0)
    @Max(Producto.MAX_STOCK)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto", nullable = false, length = 20)
    @NotNull
    private TipoProducto tipoProducto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @NotNull
    private Estado estado = Estado.ACTIVO;

    @Column(name = "imagen", length = 255)
    private String imagen;      // ruta, ej: /uploads/abc.png

    public ProductoEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}