package com.example.demo.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.admin.ReporteDiarioEntity;
import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.Entity.pedido.PedidoItemEntity;
import com.example.demo.dto.admin.ReporteDiaDto;
import com.example.demo.dto.admin.ReporteSemanaDto;
import com.example.demo.dto.admin.SemanaOpcion;
import com.example.demo.model.pedido.EstadoPedido;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ReporteDiarioRepository;

@Service
public class ReporteSemanalService {

    private static final Locale ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter CORTO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PedidoRepository pedidos;
    private final ReporteDiarioRepository reportes;

    public ReporteSemanalService(PedidoRepository pedidos, ReporteDiarioRepository reportes) {
        this.pedidos = pedidos;
        this.reportes = reportes;
    }

    public static LocalDate lunesDe(LocalDate dia) {
        return dia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @Transactional
    public ReporteSemanaDto semana(LocalDate cualquierDia) {
        LocalDate lunes = lunesDe(cualquierDia == null ? LocalDate.now() : cualquierDia);
        LocalDate lunesActual = lunesDe(LocalDate.now());
        if (lunes.isAfter(lunesActual)) {
            lunes = lunesActual;
        }
        boolean enCurso = lunes.equals(lunesActual);
        List<ReporteDiaDto> dias = enCurso ? calcular(lunes) : cerrada(lunes);
        return armar(lunes, enCurso, dias);
    }

    @Transactional(readOnly = true)
    public List<SemanaOpcion> semanasDisponibles() {
        LocalDate lunesActual = lunesDe(LocalDate.now());
        LocalDateTime primera = pedidos.primeraFecha();
        LocalDate desde = primera == null ? lunesActual : lunesDe(primera.toLocalDate());
        List<SemanaOpcion> opciones = new ArrayList<>();
        for (LocalDate l = lunesActual; !l.isBefore(desde); l = l.minusWeeks(1)) {
            String etiqueta = CORTO.format(l) + " - " + CORTO.format(l.plusDays(6))
                    + (l.equals(lunesActual) ? " (esta semana)" : "");
            opciones.add(new SemanaOpcion(l.toString(), etiqueta));
        }
        return opciones;
    }

    @Scheduled(cron = "0 5 0 * * MON")
    @Transactional
    public void cerrarSemanaAnterior() {
        cerrada(lunesDe(LocalDate.now()).minusWeeks(1));
    }

    private List<ReporteDiaDto> cerrada(LocalDate lunes) {
        LocalDate domingo = lunes.plusDays(6);
        List<ReporteDiarioEntity> guardados = reportes.findByFechaBetweenOrderByFechaAsc(lunes, domingo);
        if (guardados.size() == 7) {
            return guardados.stream().map(this::aDto).toList();
        }
        Map<LocalDate, ReporteDiarioEntity> porFecha = guardados.stream()
                .collect(Collectors.toMap(ReporteDiarioEntity::getFecha, Function.identity()));
        List<ReporteDiaDto> calculados = calcular(lunes);
        List<ReporteDiaDto> resultado = new ArrayList<>();
        for (ReporteDiaDto d : calculados) {
            ReporteDiarioEntity existente = porFecha.get(d.fecha());
            if (existente != null) {
                resultado.add(aDto(existente));
            } else {
                reportes.save(new ReporteDiarioEntity(d.fecha(), d.pedidosRecibidos(),
                        d.pedidosCompletados(), d.unidades(), d.monto()));
                resultado.add(d);
            }
        }
        return resultado;
    }

    private List<ReporteDiaDto> calcular(LocalDate lunes) {
        List<PedidoEntity> semana = pedidos.findConItemsEntre(
                lunes.atStartOfDay(), lunes.plusDays(7).atStartOfDay());
        Map<LocalDate, List<PedidoEntity>> porDia = semana.stream()
                .collect(Collectors.groupingBy(p -> p.getFecha().toLocalDate()));
        List<ReporteDiaDto> dias = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate fecha = lunes.plusDays(i);
            List<PedidoEntity> delDia = porDia.getOrDefault(fecha, List.of());
            int completados = (int) delDia.stream().filter(p -> p.getEstado() == EstadoPedido.RECOGIDO).count();
            int unidades = delDia.stream().flatMap(p -> p.getItems().stream())
                    .mapToInt(PedidoItemEntity::getCantidad).sum();
            BigDecimal monto = delDia.stream().map(PedidoEntity::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dias.add(new ReporteDiaDto(fecha, nombreDia(fecha), delDia.size(), completados, unidades, monto));
        }
        return dias;
    }

    private ReporteDiaDto aDto(ReporteDiarioEntity e) {
        return new ReporteDiaDto(e.getFecha(), nombreDia(e.getFecha()), e.getPedidosRecibidos(),
                e.getPedidosCompletados(), e.getUnidades(), e.getMonto());
    }

    private ReporteSemanaDto armar(LocalDate lunes, boolean enCurso, List<ReporteDiaDto> dias) {
        return new ReporteSemanaDto(lunes, lunes.plusDays(6), enCurso, dias,
                dias.stream().mapToInt(ReporteDiaDto::pedidosRecibidos).sum(),
                dias.stream().mapToInt(ReporteDiaDto::pedidosCompletados).sum(),
                dias.stream().mapToInt(ReporteDiaDto::unidades).sum(),
                dias.stream().map(ReporteDiaDto::monto).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private String nombreDia(LocalDate fecha) {
        String n = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, ES);
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }
}