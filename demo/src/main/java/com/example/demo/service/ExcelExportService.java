package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.dto.admin.ReporteDiaDto;
import com.example.demo.dto.admin.ReporteSemanaDto;
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

import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.Promocion;

@Service
public class ExcelExportService {

    public byte[] exportCatalog(List<Producto> products, List<Promocion> promotions) throws IOException {
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
            for (Producto p : products) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue("P" + String.format("%02d", p.getIdProducto()));
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getTipoProducto().getEtiqueta());
                var price = row.createCell(3);
                price.setCellValue(p.getPrecio().doubleValue());
                price.setCellStyle(money);
                row.createCell(4).setCellValue(p.getStock().doubleValue());
                row.createCell(5).setCellValue(p.getEstado() == Estado.ACTIVO ? "Activo" : "Inactivo");
            }
            setWidths(sheet, 4000, 11000, 4000, 4000, 3000, 4000);
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 5));


            Map<Long, String> names = products.stream()
                    .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre, (a, b) -> a));
            Sheet promoSheet = workbook.createSheet("Promociones");
            writeHeader(promoSheet, header, "ID", "Tipo", "Nombre", "Duración", "Productos", "Precio", "Estado");
            rowIndex = 1;
            for (Promocion promo : promotions) {
                Row row = promoSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue("PR" + String.format("%02d", promo.getIdPromocion()));
                row.createCell(1).setCellValue(promo.getTipoPromocion().getEtiqueta());
                row.createCell(2).setCellValue(promo.getNombre());
                row.createCell(3).setCellValue(promo.getTiempo().getEtiqueta());
                row.createCell(4).setCellValue(promo.getProductsIds().stream()
                        .map(id -> names.getOrDefault(id, String.valueOf(id)))
                        .collect(Collectors.joining(", ")));
                var price = row.createCell(5);
                price.setCellValue(promo.getPrecio().doubleValue());
                price.setCellStyle(money);
                row.createCell(6).setCellValue(promo.getEstado() == Estado.ACTIVO ? "Activo" : "Inactivo");
            }
            setWidths(promoSheet, 4000, 4000, 8000, 5000, 16000, 4000, 4000);
            promoSheet.createFreezePane(0, 1);

            workbook.write(out);
            return out.toByteArray();
        }
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

    public byte[] exportReporteSemanal(ReporteSemanaDto semana) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            XSSFFont whiteBold = workbook.createFont();
            whiteBold.setBold(true);
            whiteBold.setColor(IndexedColors.WHITE.getIndex());
            XSSFCellStyle header = workbook.createCellStyle();
            header.setFont(whiteBold);
            header.setFillForegroundColor(new XSSFColor(new byte[] { 57, 41, (byte) 181 }, null));
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont bold = workbook.createFont();
            bold.setBold(true);
            XSSFCellStyle total = workbook.createCellStyle();
            total.setFont(bold);
            XSSFCellStyle totalMoney = workbook.createCellStyle();
            totalMoney.setFont(bold);
            totalMoney.setDataFormat(workbook.createDataFormat().getFormat("\"S/ \"#,##0.00"));
            CellStyle money = workbook.createCellStyle();
            money.setDataFormat(workbook.createDataFormat().getFormat("\"S/ \"#,##0.00"));

            Sheet ventas = workbook.createSheet("Ventas");
            writeHeader(ventas, header, "Día", "Fecha", "Pedidos", "Unidades vendidas", "Monto total");
            int r = 1;
            for (ReporteDiaDto d : semana.dias()) {
                Row row = ventas.createRow(r++);
                row.createCell(0).setCellValue(d.dia());
                row.createCell(1).setCellValue(fmt.format(d.fecha()));
                row.createCell(2).setCellValue(d.pedidosRecibidos());
                row.createCell(3).setCellValue(d.unidades());
                var monto = row.createCell(4);
                monto.setCellValue(d.monto().doubleValue());
                monto.setCellStyle(money);
            }
            Row totalVentas = ventas.createRow(r);
            totalVentas.createCell(0).setCellValue("TOTAL");
            totalVentas.getCell(0).setCellStyle(total);
            for (int c = 2; c <= 4; c++) {
                String col = String.valueOf((char) ('A' + c));
                var cell = totalVentas.createCell(c);
                cell.setCellFormula("SUM(" + col + "2:" + col + "8)");
                cell.setCellStyle(c == 4 ? totalMoney : total);
            }
            setWidths(ventas, 4500, 4500, 4000, 5500, 5000);
            ventas.createFreezePane(0, 1);

            Sheet pedidos = workbook.createSheet("Pedidos");
            writeHeader(pedidos, header, "Día", "Fecha", "Pedidos recibidos", "Pedidos completados", "Pendientes");
            r = 1;
            for (ReporteDiaDto d : semana.dias()) {
                Row row = pedidos.createRow(r++);
                row.createCell(0).setCellValue(d.dia());
                row.createCell(1).setCellValue(fmt.format(d.fecha()));
                row.createCell(2).setCellValue(d.pedidosRecibidos());
                row.createCell(3).setCellValue(d.pedidosCompletados());
                row.createCell(4).setCellValue(d.pedidosRecibidos() - d.pedidosCompletados());
            }
            Row totalPedidos = pedidos.createRow(r);
            totalPedidos.createCell(0).setCellValue("TOTAL");
            totalPedidos.getCell(0).setCellStyle(total);
            for (int c = 2; c <= 4; c++) {
                String col = String.valueOf((char) ('A' + c));
                var cell = totalPedidos.createCell(c);
                cell.setCellFormula("SUM(" + col + "2:" + col + "8)");
                cell.setCellStyle(total);
            }
            setWidths(pedidos, 4500, 4500, 5500, 6000, 4500);
            pedidos.createFreezePane(0, 1);

            Sheet resumen = workbook.createSheet("Resumen");
            resumen.createRow(0).createCell(0).setCellValue("Reporte semanal K-TARO");
            resumen.getRow(0).getCell(0).setCellStyle(total);
            resumen.createRow(1).createCell(0).setCellValue("Semana");
            resumen.getRow(1).createCell(1).setCellValue(fmt.format(semana.lunes()) + " al " + fmt.format(semana.domingo()));
            resumen.createRow(2).createCell(0).setCellValue("Estado");
            resumen.getRow(2).createCell(1).setCellValue(semana.enCurso() ? "En curso" : "Cerrada");
            resumen.createRow(3).createCell(0).setCellValue("Pedidos recibidos");
            resumen.getRow(3).createCell(1).setCellValue(semana.totalRecibidos());
            resumen.createRow(4).createCell(0).setCellValue("Pedidos completados");
            resumen.getRow(4).createCell(1).setCellValue(semana.totalCompletados());
            resumen.createRow(5).createCell(0).setCellValue("Unidades vendidas");
            resumen.getRow(5).createCell(1).setCellValue(semana.totalUnidades());
            resumen.createRow(6).createCell(0).setCellValue("Monto total");
            var montoTotal = resumen.getRow(6).createCell(1);
            montoTotal.setCellValue(semana.totalMonto().doubleValue());
            montoTotal.setCellStyle(money);
            setWidths(resumen, 7000, 7000);

            workbook.setSheetOrder("Resumen", 0);
            workbook.setActiveSheet(0);

            workbook.write(out);
            return out.toByteArray();
        }
    }
}