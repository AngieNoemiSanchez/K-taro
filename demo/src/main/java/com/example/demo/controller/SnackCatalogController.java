package com.example.demo.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.Product;
import com.example.demo.service.CustomerCatalogService;

@Controller
public class SnackCatalogController {

	private final CustomerCatalogService productCatalog;

	public SnackCatalogController(CustomerCatalogService productCatalog) {
		this.productCatalog = productCatalog;
	}

	@GetMapping("/productos/{category}")
	public String catalog(@PathVariable String category, Model model) {
		CatalogCategory selectedCategory = CatalogCategory.fromSlug(category)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("activeCategory", selectedCategory.slug);
		model.addAttribute("categoryLabel", selectedCategory.label);
		model.addAttribute("snacks", productCatalog.findByCategory(selectedCategory.slug));
		return "productos/snacks";
	}

	@GetMapping("/productos/detalle/{id}")
	public String detail(@PathVariable String id, Model model, HttpServletResponse response) {
		Optional<Product> selectedProduct = productCatalog.findVisibleById(id);
		if (selectedProduct.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			model.addAttribute("productId", id);
			return "productos/no-encontrado";
		}

		Product product = selectedProduct.get();
		model.addAttribute("product", product);
		model.addAttribute("relatedProducts", productCatalog.findRelatedProducts(product));
		return "productos/detalle";
	}

	private enum CatalogCategory {
		SNACKS("snacks", "Snacks"),
		RAMEN("ramen", "Ramen"),
		BEBIDAS("bebidas", "Bebidas"),
		OTROS("otros", "Otros");

		private final String slug;
		private final String label;

		CatalogCategory(String slug, String label) {
			this.slug = slug;
			this.label = label;
		}

		private static Optional<CatalogCategory> fromSlug(String slug) {
			String normalizedSlug = slug.toLowerCase(Locale.ROOT);
			return List.of(values()).stream()
					.filter(category -> category.slug.equals(normalizedSlug))
					.findFirst();
		}
	}
}