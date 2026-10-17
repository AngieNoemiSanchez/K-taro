package com.example.demo.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PromotionForm;
import com.example.demo.model.Promotion;

/** Promociones en memoria (se pierden al reiniciar). */
@Service
public class PromotionService {

    private final List<Promotion> promotions = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    public Promotion create(PromotionForm form) {
        Promotion promotion = new Promotion(
                "promo-" + sequence.incrementAndGet(),
                form.getName().trim(),
                form.getDuration(),
                form.getDescription().trim(),
                List.copyOf(form.getProductIds()),
                form.getPrice());
        promotions.add(promotion);
        return promotion;
    }

    public List<Promotion> findAll() {
        return List.copyOf(promotions);
    }
}