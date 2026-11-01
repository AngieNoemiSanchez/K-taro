package com.example.demo.service.catalogo;

import com.example.demo.dto.OfferCard;
import com.example.demo.dto.PromotionCard;
import com.example.demo.dto.admin.PromotionForm;
import com.example.demo.model.catalogo.Promocion;

import java.util.List;
import java.util.Optional;

public interface PromocionService {
    List<Promocion> listar();
    Optional<Promocion> buscarPorId(Long id);
    Promocion crear(PromotionForm form);
    Promocion actualizar(Long id, PromotionForm form);

    /** Combos visibles en la tienda (banners). */
    List<PromotionCard> listarTarjetasVisibles();

    /** Ofertas visibles en la tienda (productos con descuento). */
    List<OfferCard> listarOfertasVisibles();
}