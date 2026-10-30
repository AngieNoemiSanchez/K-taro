package com.example.demo.controller;
import com.example.demo.service.catalogo.ProductAdminService;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ExcelExportService;
import com.example.demo.service.PromotionService;

@Controller
public class AdminExportController {

    private static final MediaType XLSX =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ExcelExportService excel;
    private final ProductAdminService productAdmin;
    private final PromotionService promotions;

    public AdminExportController(ExcelExportService excel, ProductAdminService productAdmin,
                                 PromotionService promotions) {
        this.excel = excel;
        this.productAdmin = productAdmin;
        this.promotions = promotions;
    }

    @GetMapping("/admin/catalogo/exportar")
    public ResponseEntity<byte[]> export() throws IOException {
        byte[] file = excel.exportCatalog(productAdmin.findAll(), promotions.findAll());
        String filename = "catalogo-ktaro-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(XLSX)
                .body(file);
    }
}