package com.example.demo.model.cliente;

import java.time.LocalDate;

public class Cliente {
    private Long IdCliente;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String correo;
    private String password;
    private LocalDate fechaNac;
    private String dni;

    public Cliente(Long idCliente, String nombre, String apellidos, String telefono, String direccion, String correo, String password, LocalDate fechaNac, String dni) {
        IdCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
        this.password = password;
        this.fechaNac = fechaNac;
        this.dni=dni;
    }

    public Long getIdCliente() {
        return IdCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public String getDni() {
        return dni;
    }
}
