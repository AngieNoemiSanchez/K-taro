package com.example.demo.controller.cliente;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.PedidoResumen;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.pedido.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoApiController {

    private final PedidoService pedidoService;

    public PedidoApiController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pendientes")
    public List<PedidoResumen> pendientes(Principal principal) {
        return pedidoService.pendientes(principal.getName());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> negocio(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("mensaje", ex.getMessage()));
    }
}