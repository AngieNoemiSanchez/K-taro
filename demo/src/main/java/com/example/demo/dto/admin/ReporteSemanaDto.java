package com.example.demo.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteSemanaDto(LocalDate lunes, LocalDate domingo, boolean enCurso, List<ReporteDiaDto> dias,
                               int totalRecibidos, int totalCompletados, int totalUnidades, BigDecimal totalMonto) {}