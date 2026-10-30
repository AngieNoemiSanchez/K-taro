package com.example.demo.dto.admin;

import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.TipoProducto;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public class ProductForm {

    @NotBlank(message = "Ingresa el nombre del producto")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String name;

    @NotNull(message = "Selecciona un tipo de producto")
    private TipoProducto tipoProducto;

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

    @NotNull(message = "Selecciona el estado")
    private Estado estado = Estado.ACTIVO;

    private MultipartFile image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}