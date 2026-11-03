package com.example.demo.mapper;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.example.demo.Entity.pedido.TarjetaEntity;
import com.example.demo.model.pedido.Tarjeta;

@Component
public class TarjetaMapper {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("MM/yy");

    public Tarjeta toModel(TarjetaEntity e) {
        if (e == null) return null;
        return new Tarjeta(
                e.getId(),
                e.getNumero(),
                e.getCvvHash(),
                e.getTitular(),
                aFechaVencimiento(e.getVencimiento()),
                e.getSaldo(),
                e.getEstado()
        );
    }

    public LocalDateTime aFechaVencimiento(String texto) {
        return YearMonth.parse(texto.trim(), FORMATO).atEndOfMonth().atTime(23, 59, 59);
    }

    public String aTexto(LocalDateTime fecha) {
        return fecha.format(FORMATO);
    }
}