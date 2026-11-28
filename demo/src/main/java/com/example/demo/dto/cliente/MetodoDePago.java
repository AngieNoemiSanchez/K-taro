package com.example.demo.dto.cliente;

import jakarta.validation.constraints.Pattern;

public class MetodoDePago {

    private Long tarjetaId;

    @Pattern(regexp = "\\d{14,16}", message = "El número de tarjeta debe tener entre 14 y 16 dígitos")
    private String numeroTarjeta;

    @Pattern(regexp = "\\d{3,4}", message = "El CVV debe tener 3 o 4 dígitos")
    private String cvv;

    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "La fecha debe tener el formato MM/AA")
    private String vencimiento;

    public MetodoDePago() {
    }

    public Long getTarjetaId() { return tarjetaId; }
    public void setTarjetaId(Long tarjetaId) { this.tarjetaId = tarjetaId; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getVencimiento() { return vencimiento; }
    public void setVencimiento(String vencimiento) { this.vencimiento = vencimiento; }
}