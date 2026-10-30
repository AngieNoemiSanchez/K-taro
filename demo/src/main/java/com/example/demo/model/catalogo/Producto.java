package com.example.demo.model.catalogo;


import java.math.BigDecimal;

public class Producto {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Estado estado;
    private TipoProducto tipoProducto;
    private String imagen;

    public Producto(Long idProducto, String nombre, String descripcion, BigDecimal precio,
                    Integer stock, Estado estado, TipoProducto tipoProducto, String imagen) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
        this.tipoProducto = tipoProducto;
        this.imagen = imagen;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getStock() {
        return stock;
    }

    public Estado getEstado() {
        return estado;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public String getImagen() {
        return imagen;
    }
}

