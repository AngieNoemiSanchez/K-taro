package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PerfilForm {
    @NotBlank(message = "Ingresa tu nombre")
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String nombre;

    @NotBlank(message = "Ingresa tus apellidos")
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String apellidos;

    @NotBlank(message = "Ingresa tu número telefónico")
    @Pattern(regexp = "^9\\d{8}$", message = "Ingresa un celular válido de 9 dígitos")
    private String telefono;

    @NotBlank(message = "Ingresa tu dirección")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String direccion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}