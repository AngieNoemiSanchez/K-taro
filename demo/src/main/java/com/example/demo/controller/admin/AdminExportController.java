package com.example.demo.controller.admin;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ExcelExportService;
import com.example.demo.service.catalogo.ProductoService;
import com.example.demo.service.catalogo.PromocionService;

@Controller
public class AdminExportController {

    private static final MediaType XLSX =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ExcelExportService excel;
    private final ProductoService productoService;
    private final PromocionService promotions;

    public AdminExportController(ExcelExportService excel, ProductoService productoService,
                                 PromocionService promotions) {
        this.excel = excel;
        this.productoService = productoService;
        this.promotions = promotions;
    }

    @GetMapping("/admin/catalogo/exportar")
    public ResponseEntity<byte[]> export() throws IOException {
        byte[] file = excel.exportCatalog(productoService.listar(), promotions.listar());
        String filename = "catalogo-ktaro-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(XLSX)
                .body(file);
    }
}