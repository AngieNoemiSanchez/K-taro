package com.example.demo.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MetodoDePago {

    @NotBlank(message = "Ingrese el número de la tarjeta")
    @Pattern(regexp = "\\d{16}", message = "La tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;

    @NotBlank(message = "Ingrese el CVV de la tarjeta")
    @Pattern(regexp = "\\d{3}", message = "El CVV debe tener 3 dígitos")
    private String cvv;

    @NotBlank(message = "Ingrese el nombre del titular")
    @Size(max = 60, message = "El nombre del titular no puede superar los 60 caracteres")
    private String titular;

    @NotBlank(message = "Ingrese la fecha de vencimiento")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "La fecha debe tener el formato MM/AA")
    private String vencimiento;

    public MetodoDePago() {
    }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    public String getVencimiento() { return vencimiento; }
    public void setVencimiento(String vencimiento) { this.vencimiento = vencimiento; }
}