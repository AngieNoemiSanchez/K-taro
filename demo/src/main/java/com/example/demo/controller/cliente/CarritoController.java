package com.example.demo.controller.cliente;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarritoController {

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito/carrito";
    }

    @GetMapping("/pago")
    public String pago() {
        return "pedidos/pago";
    }

    @GetMapping("/compra-exitosa")
    public String compraExitosa() {
        return "pedidos/compra-exitosa";
    }
}