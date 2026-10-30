package com.example.demo.mapper;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.dto.admin.ProductForm;
import com.example.demo.model.catalogo.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoEntity toEntity(ProductForm f) {
        ProductoEntity e = new ProductoEntity();
        e.setNombre(f.getName().trim());
        e.setDescripcion(f.getDescription().trim());
        e.setPrecio(f.getPrice());
        e.setStock(f.getStock());
        e.setTipoProducto(f.getTipoProducto());
        e.setEstado(f.getEstado());
        return e;
    }

    public void updateEntityFromForm(ProductForm f, ProductoEntity e) {
        e.setNombre(f.getName().trim());
        e.setDescripcion(f.getDescription().trim());
        e.setPrecio(f.getPrice());
        e.setStock(f.getStock());
        e.setTipoProducto(f.getTipoProducto());
        e.setEstado(f.getEstado());
    }

    public Producto toModel(ProductoEntity e) {
        if (e == null) return null;
        return new Producto(e.getId(), e.getNombre(), e.getDescripcion(), e.getPrecio(),
                e.getStock(), e.getEstado(), e.getTipoProducto(), e.getImagen());
    }

    public ProductForm toForm(Producto p) {
        ProductForm f = new ProductForm();
        f.setName(p.getNombre());
        f.setDescription(p.getDescripcion());
        f.setPrice(p.getPrecio());
        f.setStock(p.getStock());
        f.setTipoProducto(p.getTipoProducto());
        f.setEstado(p.getEstado());
        return f;
    }
}