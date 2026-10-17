package com.example.demo.dto;

import java.math.BigDecimal;

public record CatalogProduct(
		String id,
		String name,
		BigDecimal price,
		String image,
		Integer discountPercent,
		BigDecimal originalPrice) {
}
