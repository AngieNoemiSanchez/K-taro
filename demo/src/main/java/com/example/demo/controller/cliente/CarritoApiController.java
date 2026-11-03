package com.example.demo.controller.cliente;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CarritoResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.pedido.CarritoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/carrito")
public class CarritoApiController {

    public record ItemRequest(
            @NotBlank(message = "Producto inválido") String id,
            @Min(value = 0, message = "Cantidad inválida")
            @Max(value = 99, message = "La cantidad máxima por producto es 99") int quantity) {}

    private final CarritoService carritoService;

    public CarritoApiController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public CarritoResponse obtener(Principal p) {
        return carritoService.obtener(p.getName());
    }

    @PostMapping("/items")
    public CarritoResponse agregar(Principal p, @Valid @RequestBody ItemRequest r) {
        return carritoService.agregar(p.getName(), r.id(), Math.max(1, r.quantity()));
    }

    @PutMapping("/items")
    public CarritoResponse fijar(Principal p, @Valid @RequestBody ItemRequest r) {
        return carritoService.fijarCantidad(p.getName(), r.id(), r.quantity());
    }

    @DeleteMapping
    public CarritoResponse vaciar(Principal p) {
        return carritoService.vaciar(p.getName());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> negocio(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("mensaje", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst().map(e -> e.getDefaultMessage()).orElse("Datos inválidos");
        return ResponseEntity.badRequest().body(Map.of("mensaje", msg));
    }
}