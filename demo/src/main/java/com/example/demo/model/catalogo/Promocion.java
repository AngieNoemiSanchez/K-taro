package com.example.demo.model.catalogo;

import java.math.BigDecimal;
import java.util.List;

public class Promocion {
    private Long idPromocion;
    private String nombre;
    private String descripcion;
    private Estado estado;
    private BigDecimal precio;
    private Tiempo tiempo;
    private TipoPromocion tipoPromocion;
    private List<Long> productsIds;
    private String imagen;

    public Promocion(Long idPromocion, String nombre, String descripcion, Estado estado,
                     BigDecimal precio, Tiempo tiempo, TipoPromocion tipoPromocion,
                     List<Long> productsIds, String imagen) {
        this.idPromocion = idPromocion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.precio = precio;
        this.tiempo = tiempo;
        this.tipoPromocion = tipoPromocion;
        this.productsIds = productsIds;
        this.imagen = imagen;
    }

    public Long getIdPromocion() { return idPromocion; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Estado getEstado() { return estado; }
    public BigDecimal getPrecio() { return precio; }
    public Tiempo getTiempo() { return tiempo; }
    public TipoPromocion getTipoPromocion() { return tipoPromocion; }
    public List<Long> getProductsIds() { return productsIds; }
    public String getImagen() { return imagen; }
}