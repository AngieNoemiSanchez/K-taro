package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.demo.model.AdminProduct;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Promocion;

@Service
public class ExcelExportService {

    public byte[] exportCatalog(List<AdminProduct> products, List<Promocion> promotions) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFFont whiteBold = workbook.createFont();
            whiteBold.setBold(true);
            whiteBold.setColor(IndexedColors.WHITE.getIndex());
            XSSFCellStyle header = workbook.createCellStyle();
            header.setFont(whiteBold);
            header.setFillForegroundColor(new XSSFColor(new byte[] { 57, 41, (byte) 181 }, null));
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle money = workbook.createCellStyle();
            money.setDataFormat(workbook.createDataFormat().getFormat("\"S/ \"#,##0.00"));

            Sheet sheet = workbook.createSheet("Productos");
            writeHeader(sheet, header, "ID", "Nombre", "Tipo", "Precio", "Stock", "Estado");
            int rowIndex = 1;
            for (AdminProduct p : products) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(formatId("P", p.id()));
                row.createCell(1).setCellValue(p.name());
                row.createCell(2).setCellValue(capitalize(p.category()));
                var price = row.createCell(3);
                price.setCellValue(p.price().doubleValue());
                price.setCellStyle(money);
                row.createCell(4).setCellValue(p.stock().doubleValue());
                row.createCell(5).setCellValue(p.active() ? "Activo" : "Inactivo");
            }
            setWidths(sheet, 4000, 11000, 4000, 4000, 3000, 4000);
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 5));

            Map<String, String> names = products.stream()
                    .collect(Collectors.toMap(AdminProduct::id, AdminProduct::name, (a, b) -> a));
            Sheet promoSheet = workbook.createSheet("Promociones");
            writeHeader(promoSheet, header, "ID", "Nombre", "Duración", "Productos", "Precio", "Estado");
            rowIndex = 1;
            for (Promocion promo : promotions) {
                Row row = promoSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(formatId("PR", String.valueOf(promo.getIdPromocion())));
                row.createCell(1).setCellValue(promo.getNombre());
                row.createCell(2).setCellValue(promo.getTiempo().getEtiqueta());
                row.createCell(3).setCellValue(promo.getProductsIds().stream()
                        .map(id -> names.getOrDefault(String.valueOf(id), String.valueOf(id)))
                        .collect(Collectors.joining(", ")));
                var price = row.createCell(4);
                price.setCellValue(promo.getPrecio().doubleValue());
                price.setCellStyle(money);
                row.createCell(5).setCellValue(promo.getEstado() == Estado.ACTIVO ? "Activo" : "Inactivo");
            }
            setWidths(promoSheet, 4000, 8000, 5000, 16000, 4000, 4000);
            promoSheet.createFreezePane(0, 1);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static String formatId(String prefix, String id) {
        try {
            return prefix + String.format("%02d", Long.parseLong(id));
        } catch (NumberFormatException e) {
            return prefix + id;
        }
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private static void writeHeader(Sheet sheet, CellStyle style, String... titles) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            var cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(style);
        }
    }

    private static void setWidths(Sheet sheet, int... widths) {
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i]);
        }
    }
}