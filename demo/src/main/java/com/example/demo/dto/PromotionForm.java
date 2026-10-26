package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PromotionForm {

    @NotBlank(message = "Ingresa el nombre de la promoción")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String name;

    @NotBlank(message = "Selecciona la duración")
    @Pattern(regexp = "siempre|domingos|miercoles", message = "Duración no válida")
    private String duration;

    @NotBlank(message = "Ingresa la descripción")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String description;

    @NotEmpty(message = "Agrega al menos un producto a la promoción")
    private List<String> productIds = new ArrayList<>();

    @NotNull(message = "Ingresa el precio de la promoción")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 6, fraction = 2, message = "Usa como máximo 2 decimales")
    private BigDecimal price;

    private boolean active = true;

    private MultipartFile image;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getProductIds() { return productIds; }
    public void setProductIds(List<String> productIds) { this.productIds = productIds; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}