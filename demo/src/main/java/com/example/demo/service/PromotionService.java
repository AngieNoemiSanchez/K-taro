package com.example.demo.service;

import java.util.List;
import java.util.Optional;
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
                form.getPrice(),
                form.isActive());
        promotions.add(promotion);
        return promotion;
    }

    public Optional<Promotion> findById(String id) {
        return promotions.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    public Optional<Promotion> update(String id, PromotionForm form) {
        for (int i = 0; i < promotions.size(); i++) {
            if (promotions.get(i).id().equals(id)) {
                Promotion updated = new Promotion(id, form.getName().trim(), form.getDuration(),
                        form.getDescription().trim(), List.copyOf(form.getProductIds()), form.getPrice(), form.isActive());
                promotions.set(i, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public List<Promotion> findAll() {
        return List.copyOf(promotions);
    }
}