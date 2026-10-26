package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PromotionCard;
import com.example.demo.dto.PromotionForm;
import com.example.demo.model.AdminProduct;
import com.example.demo.model.Promotion;


@Service
public class PromotionService {

    private final List<Promotion> promotions = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong();
    private final ProductAdminService productAdmin;
    private final ImageStorageService images;

    public PromotionService(ProductAdminService productAdmin, ImageStorageService images) {
        this.productAdmin = productAdmin;
        this.images = images;
        seed();
    }

    public Promotion create(PromotionForm form) {
        Promotion promotion = new Promotion(
                nextId(),
                form.getName().trim(),
                form.getDuration(),
                form.getDescription().trim(),
                List.copyOf(form.getProductIds()),
                form.getPrice(),
                form.isActive(),
                images.store(form.getImage()).orElse(null));
        promotions.add(promotion);
        return promotion;
    }

    public Optional<Promotion> findById(String id) {
        return promotions.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    public Optional<Promotion> update(String id, PromotionForm form) {
        for (int i = 0; i < promotions.size(); i++) {
            Promotion current = promotions.get(i);
            if (current.id().equals(id)) {
                String image = current.image();
                Optional<String> newImage = images.store(form.getImage());
                if (newImage.isPresent()) {
                    images.delete(current.image());
                    image = newImage.get();
                }
                Promotion updated = new Promotion(id, form.getName().trim(), form.getDuration(),
                        form.getDescription().trim(), List.copyOf(form.getProductIds()), form.getPrice(),
                        form.isActive(), image);
                promotions.set(i, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public List<Promotion> findAll() {
        return List.copyOf(promotions);
    }


    public List<PromotionCard> findVisibleCards() {
        return promotions.stream()
                .filter(Promotion::active)
                .map(this::toCard)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<PromotionCard> toCard(Promotion promo) {
        List<Optional<AdminProduct>> found = promo.productIds().stream().map(productAdmin::findById).toList();
        if (found.isEmpty() || found.stream().anyMatch(o -> o.isEmpty() || !o.get().active())) {
            return Optional.empty();
        }
        List<AdminProduct> items = found.stream().map(Optional::get).toList();
        BigDecimal regularPrice = items.stream().map(AdminProduct::price).reduce(BigDecimal.ZERO, BigDecimal::add);
        int stock = items.stream().mapToInt(AdminProduct::stock).min().orElse(0);
        return Optional.of(new PromotionCard(promo.id(), promo.name(), promo.description(), promo.image(),
                promo.price(), regularPrice, durationLabel(promo.duration()),
                items.stream().map(AdminProduct::name).toList(), stock));
    }

    private static String durationLabel(String duration) {
        return switch (duration) {
            case "domingos" -> "Solo domingos";
            case "miercoles" -> "Solo miércoles";
            default -> "Siempre disponible";
        };
    }

    private String nextId() {
        return "promo-" + sequence.incrementAndGet();
    }


    private void seed() {
        promotions.add(new Promotion(nextId(), "Combo K-Drama Marathon", "siempre",
                "Disfruta tus series favoritas con nuestro combo especial. "
                        + "Incluye 1 ramen, 1 bebida coreana y 1 snack asiático.",
                List.of("shin-cup-ramyun-superstar", "jinro-lemon-soju", "korean-cracker"),
                new BigDecimal("35.00"), true, "/images/promo1.png"));
        promotions.add(new Promotion(nextId(), "Gojar Night", "siempre",
                "2x1 en Soju (sabores: original, uva, fresa, durazno), 2 snacks (salado, dulce o picante) "
                        + "y 1 bebida para mezclar (Milkis o soda).",
                List.of("jinro-lemon-soju", "korean-cracker", "malang-soft-candy-milk", "peperos-original"),
                new BigDecimal("50.00"), true, "/images/promo2.png"));
    }
}