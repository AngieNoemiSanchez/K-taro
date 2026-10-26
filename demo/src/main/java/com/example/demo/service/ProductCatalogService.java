/* package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.AdminProduct;
import com.example.demo.model.Product;


@Service
public class ProductCatalogService {

	private static final List<String> FEATURED_IDS = List.of(
			"jinro-lemon-soju", "kukhwapan", "bibigo-stir-fried-ramyun", "shin-cup-ramyun-superstar");
	private static final List<String> PROMOTIONAL_IDS = List.of(
			"shin-ramyun-toomba", "shin-ramyun-non-fried", "shin-ramyun-spicy-chicken", "malang-soft-candy-milk");

	private final ProductAdminService admin;

	public ProductCatalogService(ProductAdminService admin) {
		this.admin = admin;
	}

	public List<Product> getFeaturedProducts() {
		return findVisible(FEATURED_IDS);
	}


	public List<Product> getPromotionalProducts() {
		return findVisible(PROMOTIONAL_IDS).stream()
				.filter(p -> p.originalPrice() != null && p.discountPercent() != null)
				.toList();
	}

	public List<Product> findByCategory(String category) {
		return admin.findAllByCreation().stream()
				.filter(p -> p.active() && p.category().equals(category))
				.map(ProductCatalogService::toProduct)
				.toList();
	}


	public Optional<Product> findById(String id) {
		return admin.findById(id)
				.filter(AdminProduct::active)
				.map(ProductCatalogService::toProduct);
	}

	public List<Product> findRelatedProducts(Product product) {
		List<Product> related = product.relatedProductIds().stream()
				.map(this::findById)
				.flatMap(Optional::stream)
				.filter(r -> !r.id().equals(product.id()))
				.toList();
		if (!related.isEmpty()) {
			return related;
		}
		return findByCategory(product.category()).stream()
				.filter(r -> !r.id().equals(product.id()))
				.limit(3)
				.toList();
	}

	private List<Product> findVisible(List<String> ids) {
		return ids.stream()
				.map(this::findById)
				.flatMap(Optional::stream)
				.toList();
	}

	private static Product toProduct(AdminProduct a) {
		return new Product(a.id(), a.name(), a.description(), a.category(), a.price(),
				a.originalPrice(), a.discountPercent(), a.image(), a.stock(), a.relatedProductIds());
	}
}

*/
