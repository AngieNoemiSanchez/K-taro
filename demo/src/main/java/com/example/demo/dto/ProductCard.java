package com.example.demo.dto;

import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.TipoProducto;

import java.math.BigDecimal;

public record ProductCard(
        Long id,
        String nombre,
        String descripcion,
        String imagen,
        TipoProducto tipoProducto,
        int stock,
        BigDecimal regularPrice,
        BigDecimal price,
        boolean onOffer,
        int discountPercent,
        String cartId) {

    public static ProductCard of(Producto p, OfferCard offer) {
        int stock = p.getStock() == null ? 0 : p.getStock();
        if (offer == null) {
            return new ProductCard(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getImagen(),
                    p.getTipoProducto(), stock, p.getPrecio(), p.getPrecio(), false, 0,
                    String.valueOf(p.getIdProducto()));
        }
        return new ProductCard(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getImagen(),
                p.getTipoProducto(), stock, p.getPrecio(), offer.offerPrice(), true,
                offer.discountPercent(), offer.id());   // id "oferta-N": /api/stock ya lo entiende
    }

    public boolean inStock() {
        return stock > 0;
    }
}