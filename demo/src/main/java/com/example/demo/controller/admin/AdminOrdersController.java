package com.example.demo.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.admin.PedidoAdminFila;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.pedido.EstadoPedido;
import com.example.demo.service.pedido.AdminPedidoService;

@Controller
@RequestMapping("/admin")
public class AdminOrdersController {

    private static final String VISTA_ACTIVOS = "activos";
    private static final String VISTA_REGISTRADOS = "registrados";

    private final AdminPedidoService pedidoService;

    public AdminOrdersController(AdminPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    public String verPedidos(@RequestParam(defaultValue = VISTA_ACTIVOS) String vista, Model model) {
        boolean registrados = VISTA_REGISTRADOS.equals(vista);
        model.addAttribute("vista", registrados ? VISTA_REGISTRADOS : VISTA_ACTIVOS);
        model.addAttribute("pedidos", registrados ? pedidoService.registrados() : pedidoService.activos());
        return "admin/pedidos";
    }

    @PostMapping("/pedidos/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String estado,
                                RedirectAttributes flash) {
        try {
            PedidoAdminFila fila = pedidoService.cambiarEstado(id, parsearEstado(estado));
            if (fila.siguiente() == null) {
                flash.addFlashAttribute("ok", fila.codigo()
                        + " fue entregado y pasó a Pedidos registrados.");
            } else {
                flash.addFlashAttribute("ok", fila.codigo()
                        + " ahora está en \"" + fila.etiqueta() + "\".");
            }
        } catch (BusinessException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    private EstadoPedido parsearEstado(String valor) {
        try {
            return EstadoPedido.valueOf(valor);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("El estado indicado no es válido.");
        }
    }
}