package com.example.demo.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductForm;
import com.example.demo.model.AdminProduct;


@Service
public class ProductAdminService {

    private static final int DEFAULT_STOCK = 30;
    private static final String DEFAULT_IMAGE = "/images/logo.png";

    private final Map<String, AdminProduct> products = new ConcurrentHashMap<>();

    public ProductAdminService(ProductCatalogService catalog) {
        catalog.findAll().forEach(p -> products.put(p.id(), new AdminProduct(
                p.id(), p.name(), p.description(), p.category(), p.price(),
                DEFAULT_STOCK, true, p.image())));
    }

    public List<AdminProduct> findAll() {
        return products.values().stream()
                .sorted(Comparator.comparing(AdminProduct::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<AdminProduct> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public AdminProduct create(ProductForm form) {
        String baseId = slugify(form.getName());
        String id = baseId;
        int suffix = 2;
        AdminProduct created = toProduct(id, form, DEFAULT_IMAGE);
        while (products.putIfAbsent(id, created) != null) {
            id = baseId + "-" + suffix++;
            created = toProduct(id, form, DEFAULT_IMAGE);
        }
        return created;
    }

    public Optional<AdminProduct> update(String id, ProductForm form) {
        AdminProduct current = products.get(id);
        if (current == null) {
            return Optional.empty();
        }
        AdminProduct updated = toProduct(id, form, current.image());
        products.put(id, updated);
        return Optional.of(updated);
    }

    /** Suma {@code amount} unidades al stock actual del producto. */
    public Optional<AdminProduct> addStock(String id, int amount) {
        return Optional.ofNullable(products.computeIfPresent(id, (key, c) -> new AdminProduct(
                c.id(), c.name(), c.description(), c.category(), c.price(), c.stock() + amount, c.active(),
                c.image())));
    }

    private static AdminProduct toProduct(String id, ProductForm form, String image) {
        return new AdminProduct(id, form.getName().trim(), form.getDescription().trim(), form.getCategory(),
                form.getPrice(), form.getStock(), form.isActive(), image);
    }

    private static String slugify(String text) {
        String slug = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isEmpty() ? "producto" : slug;
    }
}