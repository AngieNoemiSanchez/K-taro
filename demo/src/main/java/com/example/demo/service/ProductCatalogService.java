package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Product;

@Service
public class ProductCatalogService {

	private static final String PENDING_DESCRIPTION = "Descripción pendiente";
	private static final List<String> SNACK_CATALOG_IDS = List.of(
			"peperos-original", "honey-butter-chip", "shrimp-crackers", "onion-rings-hot",
			"potato-snack", "cuttlefish-snack", "shrimp-flavored", "banana-snack", "korean-cracker");

	private final List<Product> products = List.of(
			product("peperos-original", "Peperos Original", PENDING_DESCRIPTION, "snacks", "6.50", null, null,
					"/images/peperos.png", "honey-butter-chip", "shrimp-crackers", "onion-rings-hot"),
			product("honey-butter-chip", "Honey Butter Chip",
					"Las papitas fritas más famosas de Corea. Ofrecen una combinación adictiva e inesperada de papas "
							+ "crujientes 100% naturales con mantequilla francesa y miel real.",
					"snacks", "12.00", "14.00", 20,
					"/images/honey butter chip.png", "shrimp-crackers", "onion-rings-hot"),
			product("shrimp-crackers", "Shrimp Crackers", PENDING_DESCRIPTION, "snacks", "6.50", null, null,
					"/images/shrimp crackers.png", "peperos-original", "potato-snack", "shrimp-flavored"),
			product("onion-rings-hot", "Onion Rings Hot 40gr.", PENDING_DESCRIPTION, "snacks", "4.02", null, null,
					"/images/onion.png", "peperos-original", "potato-snack", "cuttlefish-snack"),
			product("potato-snack", "Potato Snack 55gr.", PENDING_DESCRIPTION, "snacks", "6.00", null, null,
					"/images/Potato Snack 55gr..png", "peperos-original", "onion-rings-hot", "banana-snack"),
			product("cuttlefish-snack", "Cuttlefish Snack 45gr.", PENDING_DESCRIPTION, "snacks", "6.00", null, 20,
					"/images/Cuttlefish Snack 45gr..png", "shrimp-crackers", "shrimp-flavored", "korean-cracker"),
			product("shrimp-flavored", "Shrimp Flavored 75gr.", PENDING_DESCRIPTION, "snacks", "6.00", null, 10,
					"/images/flavored.png", "shrimp-crackers", "cuttlefish-snack", "korean-cracker"),
			product("banana-snack", "Banana 45gr.", PENDING_DESCRIPTION, "snacks", "6.00", null, null,
					"/images/banana.png", "potato-snack", "peperos-original", "korean-cracker"),
			product("korean-cracker", "Korean Cracker 192gr.", PENDING_DESCRIPTION, "snacks", "24.00", null, null,
					"/images/korean.png", "peperos-original", "shrimp-crackers", "banana-snack"),
			product("jinro-lemon-soju", "Jinro Lemon Soju 13% 360ml", PENDING_DESCRIPTION, "bebidas",
					"13.00", null, null, "/images/Rectangle 4.png",
					"kukhwapan", "shin-cup-ramyun-superstar", "malang-soft-candy-milk"),
			product("kukhwapan", "Kukhwapan 150ml", PENDING_DESCRIPTION, "snacks", "8.71", null, null,
					"/images/Rectangle 9.png",
					"peperos-original", "honey-butter-chip", "shrimp-crackers"),
			product("bibigo-stir-fried-ramyun", "Bibigo Stir-Fried Ramyun Smoky K-Chicken 122gr",
					PENDING_DESCRIPTION, "ramen", "6.00", null, null, 
					"/images/bibigo.png", "shin-ramyun-toomba", "shin-ramyun-non-fried"),
			product("shin-cup-ramyun-superstar", "Shin Cup Ramyun Superstar", PENDING_DESCRIPTION, "ramen",
					"8.00", null, null, "/images/Rectangle 16.png",
					"bibigo-stir-fried-ramyun", "shin-ramyun-toomba", "shin-ramyun-spicy-chicken"),
			product("shin-ramyun-toomba", "Shin Ramyun Toomba 137gr", PENDING_DESCRIPTION, "ramen",
					"4.30", "8.60", 50, "/images/Rectangle 7.png",
					"bibigo-stir-fried-ramyun", "shin-ramyun-non-fried", "shin-ramyun-spicy-chicken"),
			product("shin-ramyun-non-fried", "Shin Ramyun Non-Fried 97gr", PENDING_DESCRIPTION, "ramen",
					"10.50", "15.00", 30, "/images/Rectangle 12.png",
					"bibigo-stir-fried-ramyun", "shin-ramyun-toomba", "shin-ramyun-spicy-chicken"),
			product("shin-ramyun-spicy-chicken", "Shin Ramyun Spicy Chicken 120gr", PENDING_DESCRIPTION, "ramen",
					"11.20", "16.00", 30, "/images/shin.png",
					"bibigo-stir-fried-ramyun", "shin-ramyun-toomba", "shin-ramyun-non-fried"),
			product("malang-soft-candy-milk", "Malang Soft Candy Milk 79gr", PENDING_DESCRIPTION, "otros",
					"8.71", "13.00", 33, "/images/Rectangle 15.png",
					"peperos-original", "kukhwapan", "jinro-lemon-soju"));

	private final Map<String, Product> productsById = products.stream()
			.collect(Collectors.toUnmodifiableMap(Product::id, Function.identity()));

	public List<Product> getFeaturedProducts() {
		return findProducts("jinro-lemon-soju", "kukhwapan", "bibigo-stir-fried-ramyun",
				"shin-cup-ramyun-superstar");
	}

	public List<Product> getPromotionalProducts() {
		return findProducts("shin-ramyun-toomba", "shin-ramyun-non-fried", "shin-ramyun-spicy-chicken",
				"malang-soft-candy-milk");
	}

	public List<Product> getSnackProducts() {
		return findProducts(SNACK_CATALOG_IDS.toArray(String[]::new));
	}

	public Optional<Product> findById(String id) {
		return Optional.ofNullable(productsById.get(id));
	}

	public List<Product> findRelatedProducts(Product product) {
		return product.relatedProductIds().stream()
				.map(productsById::get)
				.filter(related -> related != null && !related.id().equals(product.id()))
				.toList();
	}

	private List<Product> findProducts(String... ids) {
		return java.util.Arrays.stream(ids).map(productsById::get).toList();
	}

	private static Product product(String id, String name, String description, String category, String price,
			String originalPrice, Integer discountPercent, String image, String... relatedProductIds) {
		return new Product(id, name, description, category, amount(price),
				originalPrice == null ? null : amount(originalPrice), discountPercent, image,
				List.of(relatedProductIds));
	}

	private static BigDecimal amount(String value) {
		return new BigDecimal(value);
	}
}
