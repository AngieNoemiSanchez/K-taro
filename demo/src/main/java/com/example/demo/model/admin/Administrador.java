package com.example.demo.model.admin;

import java.time.LocalDate;

public class Administrador {
    private Long idAdmin;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String dni;
    private HorarioAdmin horarioAdmin;

    public Administrador(Long idAdmin, String nombre, String apellido, String correo, String password, LocalDate fechaNacimiento, String telefono, String dni, HorarioAdmin horarioAdmin) {
        this.idAdmin = idAdmin;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.dni = dni;
        this.horarioAdmin = horarioAdmin;
    }

    public Long getIdAdmin() {
        return idAdmin;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDni() {
        return dni;
    }

    public HorarioAdmin getHorarioAdmin() {
        return horarioAdmin;
    }
}
