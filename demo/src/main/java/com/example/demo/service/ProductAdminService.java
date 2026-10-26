package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductForm;
import com.example.demo.model.AdminProduct;


@Service
public class ProductAdminService {

    private static final int DEFAULT_STOCK = 30;
    private static final String PENDING = "Descripción pendiente";

    private final Map<String, AdminProduct> products = new LinkedHashMap<>();
    private final ImageStorageService images;

    public ProductAdminService(ImageStorageService images) {
        this.images = images;
        seed();
    }


    public synchronized List<AdminProduct> findAll() {
        return products.values().stream()
                .sorted(Comparator.comparing(AdminProduct::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }


    public synchronized List<AdminProduct> findAllByCreation() {
        return List.copyOf(products.values());
    }

    public synchronized Optional<AdminProduct> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public synchronized AdminProduct create(ProductForm form) {
        String baseId = slugify(form.getName());
        String id = baseId;
        int suffix = 2;
        while (products.containsKey(id)) {
            id = baseId + "-" + suffix++;
        }
        AdminProduct created = new AdminProduct(id, form.getName().trim(), form.getDescription().trim(),
                form.getCategory(), form.getPrice(), null, null, form.getStock(), form.isActive(),
                images.store(form.getImage()).orElse(null), List.of());
        products.put(id, created);
        return created;
    }

    public synchronized Optional<AdminProduct> update(String id, ProductForm form) {
        AdminProduct current = products.get(id);
        if (current == null) {
            return Optional.empty();
        }

        String image = current.image();
        Optional<String> newImage = images.store(form.getImage());
        if (newImage.isPresent()) {
            images.delete(current.image());
            image = newImage.get();
        }


        BigDecimal originalPrice = current.originalPrice();
        Integer discount = current.discountPercent();
        if (originalPrice != null && form.getPrice().compareTo(current.price()) != 0) {
            int percent = discountPercent(originalPrice, form.getPrice());
            if (form.getPrice().compareTo(originalPrice) < 0 && percent >= 1) {
                discount = percent;
            } else {
                originalPrice = null;
                discount = null;
            }
        }

        AdminProduct updated = new AdminProduct(id, form.getName().trim(), form.getDescription().trim(),
                form.getCategory(), form.getPrice(), originalPrice, discount, form.getStock(), form.isActive(),
                image, current.relatedProductIds());
        products.put(id, updated);
        return Optional.of(updated);
    }


    public synchronized Optional<AdminProduct> addStock(String id, int amount) {
        AdminProduct c = products.get(id);
        if (c == null) {
            return Optional.empty();
        }
        AdminProduct updated = new AdminProduct(c.id(), c.name(), c.description(), c.category(), c.price(),
                c.originalPrice(), c.discountPercent(), c.stock() + amount, c.active(), c.image(),
                c.relatedProductIds());
        products.put(id, updated);
        return Optional.of(updated);
    }

    private static int discountPercent(BigDecimal original, BigDecimal price) {
        return original.subtract(price).multiply(BigDecimal.valueOf(100))
                .divide(original, 0, RoundingMode.HALF_UP).intValue();
    }

    private static String slugify(String text) {
        String slug = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isEmpty() ? "producto" : slug;
    }


    private void seed() {
        seed("peperos-original", "Peperos Original", PENDING, "snacks", "6.50", null, null,
                "/images/peperos.png", "honey-butter-chip", "shrimp-crackers", "onion-rings-hot");
        seed("honey-butter-chip", "Honey Butter Chip",
                "Las papitas fritas más famosas de Corea. Ofrecen una combinación adictiva e inesperada de papas "
                        + "crujientes 100% naturales con mantequilla francesa y miel real.",
                "snacks", "12.00", "14.00", 20,
                "/images/honey butter chip.png", "shrimp-crackers", "onion-rings-hot");
        seed("shrimp-crackers", "Shrimp Crackers", PENDING, "snacks", "6.50", null, null,
                "/images/shrimp crackers.png", "peperos-original", "potato-snack", "shrimp-flavored");
        seed("onion-rings-hot", "Onion Rings Hot 40gr.", PENDING, "snacks", "4.02", null, null,
                "/images/onion.png", "peperos-original", "potato-snack", "cuttlefish-snack");
        seed("potato-snack", "Potato Snack 55gr.", PENDING, "snacks", "6.00", null, null,
                "/images/Potato Snack 55gr..png", "peperos-original", "onion-rings-hot", "banana-snack");
        seed("cuttlefish-snack", "Cuttlefish Snack 45gr.", PENDING, "snacks", "6.00", null, 20,
                "/images/Cuttlefish Snack 45gr..png", "shrimp-crackers", "shrimp-flavored", "korean-cracker");
        seed("shrimp-flavored", "Shrimp Flavored 75gr.", PENDING, "snacks", "6.00", null, 10,
                "/images/flavored.png", "shrimp-crackers", "cuttlefish-snack", "korean-cracker");
        seed("banana-snack", "Banana 45gr.", PENDING, "snacks", "6.00", null, null,
                "/images/banana.png", "potato-snack", "peperos-original", "korean-cracker");
        seed("korean-cracker", "Korean Cracker 192gr.", PENDING, "snacks", "24.00", null, null,
                "/images/korean.png", "peperos-original", "shrimp-crackers", "banana-snack");
        seed("jinro-lemon-soju", "Jinro Lemon Soju 13% 360ml", PENDING, "bebidas", "13.00", null, null,
                "/images/Rectangle 4.png", "kukhwapan", "shin-cup-ramyun-superstar", "malang-soft-candy-milk");
        seed("kukhwapan", "Kukhwapan 150ml", PENDING, "snacks", "8.71", null, null,
                "/images/Rectangle 9.png", "peperos-original", "honey-butter-chip", "shrimp-crackers");
        seed("bibigo-stir-fried-ramyun", "Bibigo Stir-Fried Ramyun Smoky K-Chicken 122gr", PENDING, "ramen",
                "6.00", null, null,
                "/images/bibigo.png", "shin-ramyun-toomba", "shin-ramyun-non-fried");
        seed("shin-cup-ramyun-superstar", "Shin Cup Ramyun Superstar", PENDING, "ramen", "8.00", null, null,
                "/images/Rectangle 16.png", "bibigo-stir-fried-ramyun", "shin-ramyun-toomba",
                "shin-ramyun-spicy-chicken");
        seed("shin-ramyun-toomba", "Shin Ramyun Toomba 137gr", PENDING, "ramen", "4.30", "8.60", 50,
                "/images/Rectangle 7.png", "bibigo-stir-fried-ramyun", "shin-ramyun-non-fried",
                "shin-ramyun-spicy-chicken");
        seed("shin-ramyun-non-fried", "Shin Ramyun Non-Fried 97gr", PENDING, "ramen", "10.50", "15.00", 30,
                "/images/Rectangle 12.png", "bibigo-stir-fried-ramyun", "shin-ramyun-toomba",
                "shin-ramyun-spicy-chicken");
        seed("shin-ramyun-spicy-chicken", "Shin Ramyun Spicy Chicken 120gr", PENDING, "ramen", "11.20", "16.00", 30,
                "/images/shin.png", "bibigo-stir-fried-ramyun", "shin-ramyun-toomba", "shin-ramyun-non-fried");
        seed("malang-soft-candy-milk", "Malang Soft Candy Milk 79gr", PENDING, "otros", "8.71", "13.00", 33,
                "/images/Rectangle 15.png", "peperos-original", "kukhwapan", "jinro-lemon-soju");
    }

    private void seed(String id, String name, String description, String category, String price,
                      String originalPrice, Integer discountPercent, String image, String... related) {
        products.put(id, new AdminProduct(id, name, description, category, new BigDecimal(price),
                originalPrice == null ? null : new BigDecimal(originalPrice), discountPercent,
                DEFAULT_STOCK, true, image, List.of(related)));
    }
}