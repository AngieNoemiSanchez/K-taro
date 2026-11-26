package com.example.demo.dto.cliente;

import com.example.demo.validation.PasswordSegura;
import jakarta.validation.constraints.NotBlank;

public class RestablecerForm {

    @NotBlank
    private String token;

    @NotBlank(message = "Ingresa tu nueva contraseña")
    @PasswordSegura
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