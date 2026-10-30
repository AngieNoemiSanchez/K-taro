package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.AdminProduct;
import com.example.demo.model.Product;
import com.example.demo.service.catalogo.ProductAdminService;

/**
 * Vista de cliente del catálogo. Lee de {@link ProductAdminService} y solo
 * expone productos ACTIVOS; si un producto se inactiva en el admin, desaparece de aquí.
 */
@Service
public class CustomerCatalogService {

    private static final List<String> FEATURED_IDS = List.of(
            "jinro-lemon-soju", "kukhwapan", "bibigo-stir-fried-ramyun", "shin-cup-ramyun-superstar");
    private static final List<String> PROMOTIONAL_IDS = List.of(
            "shin-ramyun-toomba", "shin-ramyun-non-fried", "shin-ramyun-spicy-chicken", "malang-soft-candy-milk");

    private final ProductAdminService admin;

    public CustomerCatalogService(ProductAdminService admin) {
        this.admin = admin;
    }

    public List<Product> getFeaturedProducts() {
        return findVisible(FEATURED_IDS);
    }

    /** Productos con descuento para el home (necesitan precio original y porcentaje). */
    public List<Product> getPromotionalProducts() {
        return findVisible(PROMOTIONAL_IDS).stream()
                .filter(p -> p.originalPrice() != null && p.discountPercent() != null)
                .toList();
    }

    public List<Product> findByCategory(String category) {
        return admin.findAllByCreation().stream()
                .filter(p -> p.active() && p.category().equals(category))
                .map(CustomerCatalogService::toProduct)
                .toList();
    }

    /** Vacío si no existe o está inactivo. */
    public Optional<Product> findVisibleById(String id) {
        return admin.findById(id)
                .filter(AdminProduct::active)
                .map(CustomerCatalogService::toProduct);
    }

    public List<Product> findRelatedProducts(Product product) {
        List<Product> related = product.relatedProductIds().stream()
                .map(this::findVisibleById)
                .flatMap(Optional::stream)
                .filter(r -> !r.id().equals(product.id()))
                .toList();
        if (!related.isEmpty()) {
            return related;
        }
        // Productos nuevos no traen relacionados: se sugieren otros de la misma categoría.
        return findByCategory(product.category()).stream()
                .filter(r -> !r.id().equals(product.id()))
                .limit(3)
                .toList();
    }

    private List<Product> findVisible(List<String> ids) {
        return ids.stream()
                .map(this::findVisibleById)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Product toProduct(AdminProduct a) {
        return new Product(a.id(), a.name(), a.description(), a.category(), a.price(),
                a.originalPrice(), a.discountPercent(), a.image(), a.stock(), a.relatedProductIds());
    }
}