package com.example.demo.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class ProductForm {

    @NotBlank(message = "Ingresa el nombre del producto")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String name;

    @NotBlank(message = "Selecciona un tipo de producto")
    @Pattern(regexp = "snacks|ramen|bebidas|otros", message = "Tipo de producto no válido")
    private String category;

    @NotBlank(message = "Ingresa la descripción")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String description;

    @NotNull(message = "Ingresa el precio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 6, fraction = 2, message = "Usa como máximo 2 decimales")
    private BigDecimal price;

    @NotNull(message = "Ingresa el stock")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private boolean active = true;


    private MultipartFile image;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}