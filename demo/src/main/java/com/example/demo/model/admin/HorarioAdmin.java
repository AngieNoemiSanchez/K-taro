package com.example.demo.model.admin;

import java.time.LocalTime;

public class HorarioAdmin {
    private Long idHorario;
    private String dias;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public HorarioAdmin(Long idHorario, String dias, LocalTime horaInicio, LocalTime horaFin) {
        this.idHorario = idHorario;
        this.dias = dias;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getIdHorario() {
        return idHorario;
    }

    public String getDias() {
        return dias;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }
}
