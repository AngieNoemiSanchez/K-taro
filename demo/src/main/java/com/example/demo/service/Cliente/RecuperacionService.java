package com.example.demo.service.Cliente;


public interface RecuperacionService {
    void solicitar(String correo);
    boolean tokenValido(String token);
    void restablecer(String token, String nuevaPassword);
}