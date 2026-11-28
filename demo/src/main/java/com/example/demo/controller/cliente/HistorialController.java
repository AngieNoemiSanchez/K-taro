package com.example.demo.controller.cliente;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.BoletaDetalle;
import com.example.demo.dto.HistorialBoleta;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.BoletaPdfService;
import com.example.demo.service.pedido.HistorialService;

@Controller
@RequestMapping("/cliente/perfil/historial")
public class HistorialController {

    private final HistorialService historialService;
    private final BoletaPdfService boletaPdfService;

    public HistorialController(HistorialService historialService, BoletaPdfService boletaPdfService) {
        this.historialService = historialService;
        this.boletaPdfService = boletaPdfService;
    }

    @GetMapping
    public String ver(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                      Principal principal,
                      Model model) {
        boolean rangoInvalido = desde != null && hasta != null && desde.isAfter(hasta);
        List<HistorialBoleta> boletas = rangoInvalido
                ? List.of()
                : historialService.listar(principal.getName(), desde, hasta);

        model.addAttribute("boletas", boletas);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("rangoInvalido", rangoInvalido);
        return "cliente/historial";
    }

    @GetMapping("/{id}/boleta")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id, Principal principal) throws IOException {
        BoletaDetalle boleta;
        try {
            boleta = historialService.boleta(id, principal.getName());
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/cliente/perfil/historial?noencontrado")
                    .build();
        }

        byte[] archivo = boletaPdfService.generar(boleta);
        String nombre = "boleta-ktaro-" + String.format("%05d", id) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombre).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }
}