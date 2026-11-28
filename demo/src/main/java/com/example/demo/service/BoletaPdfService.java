package com.example.demo.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BoletaDetalle;

@Service
public class BoletaPdfService {

    private static final float ANCHO = PDRectangle.A4.getWidth();
    private static final float ALTO = PDRectangle.A4.getHeight();
    private static final float MARGEN = 50f;
    private static final float COL_CANTIDAD = 345f;
    private static final float COL_PRECIO = 445f;
    private static final float COL_SUBTOTAL = ANCHO - MARGEN - 8f;
    private static final float ANCHO_PRODUCTO = 255f;

    private static final Color MORADO = new Color(57, 41, 181);
    private static final Color LAVANDA = new Color(221, 227, 247);
    private static final Color GRIS = new Color(110, 110, 110);
    private static final Color LINEA = new Color(225, 225, 232);

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(BoletaDetalle boleta) throws IOException {
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            try (Hoja hoja = new Hoja(documento, normal, negrita)) {
                encabezado(hoja, boleta);
                datos(hoja, boleta);
                tabla(hoja, boleta);
                total(hoja, boleta);
                hoja.textoCentrado("Gracias por tu compra en K-TARO", 30f, normal, 10, GRIS);
            }

            documento.save(salida);
            return salida.toByteArray();
        }
    }

    private void encabezado(Hoja hoja, BoletaDetalle boleta) throws IOException {
        hoja.rectangulo(0, ALTO - 95, ANCHO, 95, MORADO);
        hoja.texto("K-TARO", MARGEN, ALTO - 52, hoja.negrita, 26, Color.WHITE);
        hoja.texto("Comprobante de pedido", MARGEN, ALTO - 74, hoja.normal, 11, Color.WHITE);
        hoja.textoDerecha(boleta.codigo(), ANCHO - MARGEN, ALTO - 52, hoja.negrita, 15, Color.WHITE);
        hoja.textoDerecha(boleta.fecha().format(FECHA), ANCHO - MARGEN, ALTO - 74, hoja.normal, 11, Color.WHITE);
        hoja.y = ALTO - 95 - 38;
    }

    private void datos(Hoja hoja, BoletaDetalle boleta) throws IOException {
        hoja.texto("Datos del cliente", MARGEN, hoja.y, hoja.negrita, 12, MORADO);
        hoja.y -= 20;
        fila(hoja, "Cliente", boleta.cliente());
        fila(hoja, "DNI", boleta.dni());
        fila(hoja, "Correo", boleta.correo());

        hoja.y -= 14;
        hoja.texto("Datos del pedido", MARGEN, hoja.y, hoja.negrita, 12, MORADO);
        hoja.y -= 20;
        fila(hoja, "Pedido", boleta.codigo());
        fila(hoja, "Fecha", boleta.fecha().format(FECHA));
        fila(hoja, "Método de pago", boleta.metodoPago());
        fila(hoja, "Estado", "Recogido");
        hoja.y -= 18;
    }

    private void fila(Hoja hoja, String etiqueta, String valor) throws IOException {
        hoja.texto(etiqueta, MARGEN, hoja.y, hoja.negrita, 10, GRIS);
        hoja.texto(valor, MARGEN + 110, hoja.y, hoja.normal, 10, Color.BLACK);
        hoja.y -= 16;
    }

    private void tabla(Hoja hoja, BoletaDetalle boleta) throws IOException {
        cabeceraTabla(hoja);

        for (BoletaDetalle.Item item : boleta.items()) {
            List<String> lineas = hoja.partir(item.nombre(), hoja.normal, 10, ANCHO_PRODUCTO);
            float alto = (lineas.size() - 1) * 14f + 24f;

            if (hoja.y - alto < MARGEN + 90) {
                hoja.nueva();
                cabeceraTabla(hoja);
            }

            float base = hoja.y;
            for (int i = 0; i < lineas.size(); i++) {
                hoja.texto(lineas.get(i), MARGEN + 8, base - i * 14f, hoja.normal, 10, Color.BLACK);
            }
            hoja.textoDerecha(String.valueOf(item.cantidad()), COL_CANTIDAD, base, hoja.normal, 10, Color.BLACK);
            hoja.textoDerecha(moneda(item.precioUnitario()), COL_PRECIO, base, hoja.normal, 10, Color.BLACK);
            hoja.textoDerecha(moneda(item.subtotal()), COL_SUBTOTAL, base, hoja.normal, 10, Color.BLACK);

            float fin = base - (lineas.size() - 1) * 14f - 8f;
            hoja.linea(MARGEN, ANCHO - MARGEN, fin, LINEA);
            hoja.y = fin - 16f;
        }
    }

    private void cabeceraTabla(Hoja hoja) throws IOException {
        float base = hoja.y;
        hoja.rectangulo(MARGEN, base - 8, ANCHO - 2 * MARGEN, 26, MORADO);
        hoja.texto("Producto", MARGEN + 8, base, hoja.negrita, 10, Color.WHITE);
        hoja.textoDerecha("Cant.", COL_CANTIDAD, base, hoja.negrita, 10, Color.WHITE);
        hoja.textoDerecha("Precio unit.", COL_PRECIO, base, hoja.negrita, 10, Color.WHITE);
        hoja.textoDerecha("Subtotal", COL_SUBTOTAL, base, hoja.negrita, 10, Color.WHITE);
        hoja.y -= 32;
    }

    private void total(Hoja hoja, BoletaDetalle boleta) throws IOException {
        if (hoja.y < MARGEN + 70) {
            hoja.nueva();
        }
        hoja.rectangulo(MARGEN, hoja.y - 10, ANCHO - 2 * MARGEN, 30, LAVANDA);
        hoja.texto("TOTAL PAGADO", MARGEN + 8, hoja.y, hoja.negrita, 12, MORADO);
        hoja.textoDerecha(moneda(boleta.total()), COL_SUBTOTAL, hoja.y, hoja.negrita, 13, MORADO);
        hoja.y -= 40;
    }

    private static String moneda(BigDecimal valor) {
        return String.format(Locale.US, "S/ %,.2f", valor);
    }

    private static String limpiar(String texto, PDType1Font fuente) {
        if (texto == null) {
            return "";
        }
        StringBuilder limpio = new StringBuilder();
        texto.codePoints().forEach(cp -> {
            if (Character.isISOControl(cp)) {
                limpio.append(' ');
                return;
            }
            try {
                fuente.encode(new String(Character.toChars(cp)));
                limpio.appendCodePoint(cp);
            } catch (IOException | IllegalArgumentException e) {
                limpio.append('?');
            }
        });
        return limpio.toString();
    }

    private static float ancho(String texto, PDType1Font fuente, float tamano) throws IOException {
        return fuente.getStringWidth(texto) / 1000f * tamano;
    }

    private static final class Hoja implements AutoCloseable {

        private final PDDocument documento;
        private final PDType1Font normal;
        private final PDType1Font negrita;
        private PDPageContentStream flujo;
        private float y;

        Hoja(PDDocument documento, PDType1Font normal, PDType1Font negrita) throws IOException {
            this.documento = documento;
            this.normal = normal;
            this.negrita = negrita;
            nueva();
        }

        void nueva() throws IOException {
            if (flujo != null) {
                flujo.close();
            }
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            flujo = new PDPageContentStream(documento, pagina);
            y = ALTO - MARGEN;
        }

        void texto(String texto, float x, float posY, PDType1Font fuente, float tamano, Color color)
                throws IOException {
            flujo.beginText();
            flujo.setFont(fuente, tamano);
            flujo.setNonStrokingColor(color);
            flujo.newLineAtOffset(x, posY);
            flujo.showText(limpiar(texto, fuente));
            flujo.endText();
        }

        void textoDerecha(String texto, float xDerecha, float posY, PDType1Font fuente, float tamano, Color color)
                throws IOException {
            String limpio = limpiar(texto, fuente);
            texto(limpio, xDerecha - ancho(limpio, fuente, tamano), posY, fuente, tamano, color);
        }

        void textoCentrado(String texto, float posY, PDType1Font fuente, float tamano, Color color)
                throws IOException {
            String limpio = limpiar(texto, fuente);
            texto(limpio, (ANCHO - ancho(limpio, fuente, tamano)) / 2f, posY, fuente, tamano, color);
        }

        void rectangulo(float x, float posY, float ancho, float alto, Color color) throws IOException {
            flujo.setNonStrokingColor(color);
            flujo.addRect(x, posY, ancho, alto);
            flujo.fill();
        }

        void linea(float x1, float x2, float posY, Color color) throws IOException {
            flujo.setStrokingColor(color);
            flujo.setLineWidth(0.7f);
            flujo.moveTo(x1, posY);
            flujo.lineTo(x2, posY);
            flujo.stroke();
        }

        List<String> partir(String texto, PDType1Font fuente, float tamano, float maximo) throws IOException {
            List<String> lineas = new ArrayList<>();
            StringBuilder actual = new StringBuilder();

            for (String palabra : cortarPalabrasLargas(limpiar(texto, fuente), fuente, tamano, maximo)) {
                String candidata = actual.length() == 0 ? palabra : actual + " " + palabra;
                if (actual.length() == 0 || ancho(candidata, fuente, tamano) <= maximo) {
                    actual = new StringBuilder(candidata);
                } else {
                    lineas.add(actual.toString());
                    actual = new StringBuilder(palabra);
                }
            }
            if (actual.length() > 0) {
                lineas.add(actual.toString());
            }
            if (lineas.isEmpty()) {
                lineas.add("");
            }
            return lineas;
        }

        private List<String> cortarPalabrasLargas(String texto, PDType1Font fuente, float tamano, float maximo)
                throws IOException {
            List<String> palabras = new ArrayList<>();
            for (String palabra : texto.trim().split("\\s+")) {
                String resto = palabra;
                while (resto.length() > 1 && ancho(resto, fuente, tamano) > maximo) {
                    int corte = resto.length() - 1;
                    while (corte > 1 && ancho(resto.substring(0, corte), fuente, tamano) > maximo) {
                        corte--;
                    }
                    palabras.add(resto.substring(0, corte));
                    resto = resto.substring(corte);
                }
                palabras.add(resto);
            }
            return palabras;
        }

        @Override
        public void close() throws IOException {
            if (flujo != null) {
                flujo.close();
            }
        }
    }
}