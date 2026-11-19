package com.example.demo.controller.admin;

import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.model.pedido.EstadoPedido;
import com.example.demo.repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    private final PedidoRepository pedidoRepository;

    public DashboardController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String verDashboard(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<PedidoEntity> pedidosSemana = pedidoRepository.findByFechaBetween(
                lunes.atStartOfDay(),
                domingo.atTime(LocalTime.MAX)
        );

        int[] recibidos = new int[7];
        int[] completados = new int[7];
        BigDecimal[] montos = new BigDecimal[7];
        for (int i = 0; i < 7; i++) {
            montos[i] = BigDecimal.ZERO;
        }

        for (PedidoEntity p : pedidosSemana) {
            if (p.getFecha() == null) {
                continue;
            }
            int idx = p.getFecha().getDayOfWeek().getValue() - 1; // Lunes = 0 ... Domingo = 6
            recibidos[idx]++;

            if (p.getTotal() != null) {
                montos[idx] = montos[idx].add(p.getTotal());
            }

            if (p.getEstado() == EstadoPedido.RECOGIDO) {
                completados[idx]++;
            }
        }

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        DecimalFormat formatoSoles = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.US));

        List<Integer> ventasValues = new ArrayList<>();
        List<List<String>> ventasRows = new ArrayList<>();
        List<Integer> pedidosValues = new ArrayList<>();
        List<List<String>> pedidosRows = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            ventasValues.add(recibidos[i]);
            ventasRows.add(List.of(
                    dias[i],
                    String.valueOf(recibidos[i]),
                    "S/ " + formatoSoles.format(montos[i])
            ));

            pedidosValues.add(recibidos[i]);
            pedidosRows.add(List.of(
                    dias[i],
                    String.valueOf(recibidos[i]),
                    String.valueOf(completados[i])
            ));
        }

        model.addAttribute("ventasValues", ventasValues);
        model.addAttribute("ventasRows", ventasRows);
        model.addAttribute("pedidosValues", pedidosValues);
        model.addAttribute("pedidosRows", pedidosRows);

        return "admin/dashboard";
    }
}