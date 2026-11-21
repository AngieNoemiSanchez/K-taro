package com.example.demo.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PerfilForm {

    @NotBlank(message = "Ingresa tu correo electrónico")
    @Size(max = 50, message = "Máximo 50 caracteres")
    @Email(message = "Ingresa un correo válido")
    private String correo;

    @NotBlank(message = "Ingresa tu número telefónico")
    @Pattern(regexp = "^9\\d{8}$", message = "Ingresa un celular válido de 9 dígitos")
    private String telefono;

    @NotBlank(message = "Ingresa tu dirección")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String direccion;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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