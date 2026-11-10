package com.example.demo.controller.cliente;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.exception.BusinessException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.pedido.PedidoService;

@Controller
public class CarritoController {

    private final ClienteRepository clienteRepository;
    private final PedidoService pedidoService;

    public CarritoController(ClienteRepository clienteRepository, PedidoService pedidoService) {
        this.clienteRepository = clienteRepository;
        this.pedidoService = pedidoService;
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
    public String compraExitosaSinId() {
        return "redirect:/carrito";
    }

    @GetMapping("/compra-exitosa/{id}")
    public String compraExitosa(@PathVariable Long id, Principal principal, Model model) {
        try {
            model.addAttribute("pedido", pedidoService.detalle(id, principal.getName()));
        } catch (BusinessException e) {
            return "redirect:/carrito";
        }
        return "pedidos/compra-exitosa";
    }
}