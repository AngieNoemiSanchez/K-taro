package com.example.demo.mapper;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.Entity.catalogo.PromocionEntity;
import com.example.demo.dto.admin.PromotionForm;
import com.example.demo.model.catalogo.Promocion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PromocionMapper {

    public Promocion toModel(PromocionEntity e) {
        if (e == null) return null;
        return new Promocion(e.getId(), e.getNombre(), e.getDescripcion(), e.getEstado(),
                e.getPrecio(), e.getTiempo(), e.getTipoPromocion(),
                e.getProductos().stream().map(ProductoEntity::getId).toList(),
                e.getImagen());
    }

    public PromotionForm toForm(Promocion p) {
        PromotionForm f = new PromotionForm();
        f.setNombre(p.getNombre());
        f.setTiempo(p.getTiempo());
        f.setTipoPromocion(p.getTipoPromocion());
        f.setDescripcion(p.getDescripcion());
        f.setProductIds(new ArrayList<>(p.getProductsIds()));
        f.setPrecio(p.getPrecio());
        f.setEstado(p.getEstado());
        return f;
    }
}