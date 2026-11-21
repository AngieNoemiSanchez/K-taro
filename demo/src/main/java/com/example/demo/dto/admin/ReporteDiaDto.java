package com.example.demo.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteDiaDto(LocalDate fecha, String dia, int pedidosRecibidos, int pedidosCompletados,
                            int unidades, BigDecimal monto) {}