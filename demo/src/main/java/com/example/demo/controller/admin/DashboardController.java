package com.example.demo.controller.admin;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.admin.ReporteSemanaDto;
import com.example.demo.service.ExcelExportService;
import com.example.demo.service.ReporteSemanalService;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private static final MediaType XLSX =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteSemanalService reportes;
    private final ExcelExportService excel;

    public DashboardController(ReporteSemanalService reportes, ExcelExportService excel) {
        this.reportes = reportes;
        this.excel = excel;
    }

    @GetMapping
    public String verDashboard(Model model) {
        model.addAttribute("semanas", reportes.semanasDisponibles());
        return "admin/dashboard";
    }

    @GetMapping("/datos")
    @ResponseBody
    public ReporteSemanaDto datos(@RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana) {
        return reportes.semana(semana);
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam(required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana)
            throws IOException {
        ReporteSemanaDto reporte = reportes.semana(semana);
        String nombre = "reporte-semanal-ktaro-" + reporte.lunes() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombre).build().toString())
                .contentType(XLSX)
                .body(excel.exportReporteSemanal(reporte));
    }
}