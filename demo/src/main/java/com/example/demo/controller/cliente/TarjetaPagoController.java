package com.example.demo.controller.cliente;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.cliente.TarjetaVista;
import com.example.demo.service.pedido.TarjetaService;

@RestController
@RequestMapping("/api/pedidos/tarjetas")
public class TarjetaPagoController {

    public record Respuesta(List<TarjetaVista> tarjetas, boolean puedeAgregar) {
    }

    private final TarjetaService tarjetaService;

    public TarjetaPagoController(TarjetaService tarjetaService) {
        this.tarjetaService = tarjetaService;
    }

    @GetMapping
    public Respuesta listar(Principal principal) {
        List<TarjetaVista> tarjetas = tarjetaService.listar(principal.getName());
        return new Respuesta(tarjetas, tarjetas.size() < TarjetaService.MAX_TARJETAS);
    }
}