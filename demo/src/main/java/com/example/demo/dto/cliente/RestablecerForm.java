package com.example.demo.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RestablecerForm {

    @NotBlank
    private String token;

    @NotBlank(message = "Ingresa tu nueva contraseña")
    @Size(min = 6, max = 30, message = "Entre 6 y 30 caracteres")
    private String password;

    @NotBlank(message = "Confirma tu nueva contraseña")
    private String confirmarPassword;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmarPassword() { return confirmarPassword; }
    public void setConfirmarPassword(String confirmarPassword) { this.confirmarPassword = confirmarPassword; }
}