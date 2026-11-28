package com.example.demo.model.pedido;

public enum TipoTarjeta {
    VISA("Visa", 16, 3),
    MASTERCARD("Mastercard", 16, 3),
    AMERICAN_EXPRESS("American Express", 15, 4),
    DINERS_CLUB("Diners Club", 14, 3);

    private final String etiqueta;
    private final int longitud;
    private final int longitudCvv;

    TipoTarjeta(String etiqueta, int longitud, int longitudCvv) {
        this.etiqueta = etiqueta;
        this.longitud = longitud;
        this.longitudCvv = longitudCvv;
    }

    public String getEtiqueta() { return etiqueta; }
    public int getLongitud() { return longitud; }
    public int getLongitudCvv() { return longitudCvv; }

    public static TipoTarjeta detectar(String numero) {
        if (numero == null) {
            return null;
        }
        String digitos = numero.replaceAll("\\D", "");
        if (digitos.startsWith("34") || digitos.startsWith("37")) {
            return AMERICAN_EXPRESS;
        }
        if (digitos.matches("^(30[0-5]|3095|36|38|39).*")) {
            return DINERS_CLUB;
        }
        if (digitos.startsWith("4")) {
            return VISA;
        }
        if (digitos.matches("^5[1-5].*") || esRangoMastercard(digitos)) {
            return MASTERCARD;
        }
        return null;
    }

    private static boolean esRangoMastercard(String digitos) {
        if (digitos.length() < 4) {
            return false;
        }
        int prefijo = Integer.parseInt(digitos.substring(0, 4));
        return prefijo >= 2221 && prefijo <= 2720;
    }
}