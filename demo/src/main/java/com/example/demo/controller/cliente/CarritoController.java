package com.example.demo.controller.cliente;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.ClienteRepository;

@Controller
public class CarritoController {

    private final ClienteRepository clienteRepository;

    public CarritoController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "pedidos/carrito";
    }

    @GetMapping("/pago")
    public String pago(Principal principal, Model model) {
        String correo = principal != null ? principal.getName() : "";
        String nombre = clienteRepository.findByCorreo(correo)
                .map(c -> c.getNombre() + " " + c.getApellidos())
                .orElse("");

        model.addAttribute("correo", correo);
        model.addAttribute("nombreCliente", nombre);
        return "pedidos/pago";
    }

    @GetMapping("/compra-exitosa")
    public String compraExitosa() {
        return "pedidos/compra-exitosa";
    }
}