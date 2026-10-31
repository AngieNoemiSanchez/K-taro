package com.example.demo.dto.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Tiempo;
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
    private String nombre;

    @NotNull(message = "Selecciona el tiempo de duración")
    private Tiempo tiempo = Tiempo.siempre;

    @NotBlank(message = "Ingresa la descripción")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String descripcion;

    @NotEmpty(message = "Agrega al menos un producto a la promoción")
    private List<Long> productIds = new ArrayList<>();

    @NotNull(message = "Ingresa el precio de la promoción")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 6, fraction = 2, message = "Usa como máximo 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "Selecciona el estado")
    private Estado estado = Estado.ACTIVO;

    private MultipartFile image;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tiempo getTiempo() {
        return tiempo;
    }

    public void setTiempo(Tiempo tiempo) {
        this.tiempo = tiempo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

        public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
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