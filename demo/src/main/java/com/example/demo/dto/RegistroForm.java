package com.example.demo.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RegistroForm {
    @NotBlank(message = "Ingresa tu nombre")
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String nombre;

    @NotBlank(message = "Ingresa tus apellidos")
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String apellidos;

    @NotBlank(message = "Ingresa tu número telefonico")
    @Pattern(regexp = "^9\\d{8}$", message = "Ingresa un celular válido de 9 dígitos")
    private String telefono;

    @NotBlank(message = "Ingresa tu dirección")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String direccion;

    @NotBlank(message = "Ingresa tu correo electrónico")
    @Size(max = 50, message = "Máximo 50 caracteres")
    @Email(message = "Ingresa un correo válido")
    private String correo;

    @NotBlank(message = "Ingresa tus contraseña")
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String password;


    @NotBlank (message = "Ingresa tu DNI")
    @Size(max = 8 , message = "Máximo 8 caracteres")
    private String dni;

    @NotNull(message = "Ingresa tu fecha de nacimiento")
    @Past(message = "La fecha debe ser anterior a hoy")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
