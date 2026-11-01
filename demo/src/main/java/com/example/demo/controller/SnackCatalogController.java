package com.example.demo.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.TipoProducto;
import com.example.demo.service.CustomerCatalogService;

@Controller
public class SnackCatalogController {

	private final CustomerCatalogService productCatalog;

	public SnackCatalogController(CustomerCatalogService productCatalog) {
		this.productCatalog = productCatalog;
	}

	@GetMapping("/productos/{category}")
	public String catalog(@PathVariable String category, Model model) {
		TipoProducto tipo = TipoProducto.fromSlug(category)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("activeCategory", tipo.getSlug());
		model.addAttribute("categoryLabel", tipo.getEtiqueta());
		model.addAttribute("snacks", productCatalog.findByCategory(tipo));
		return "catalogo/snacks";
	}

	@GetMapping("/productos/detalle/{id}")
	public String detail(@PathVariable String id, Model model, HttpServletResponse response) {
		Optional<Producto> selected = productCatalog.findVisibleById(id);
		if (selected.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			model.addAttribute("productId", id);
			return "catalogo/no-encontrado";
		}

		Producto product = selected.get();
		model.addAttribute("product", product);
		model.addAttribute("relatedProducts", productCatalog.findRelatedProducts(product));
		return "catalogo/detalle";
	}
}