package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;

public record Product(
		String id,
		String name,
		String description,
		String category,
		BigDecimal price,
		BigDecimal originalPrice,
		Integer discountPercent,
		String image,
		Integer stock,
		List<String> relatedProductIds) {


	public boolean inStock() {
		return stock != null && stock > 0;
	}
}