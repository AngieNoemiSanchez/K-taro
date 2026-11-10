package com.example.demo.service.pedido;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.dto.PedidoDetalle;
import com.example.demo.dto.PedidoResumen;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.pedido.EstadoPedido;
import com.example.demo.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidos;

    public PedidoService(PedidoRepository pedidos) {
        this.pedidos = pedidos;
    }

    public static String codigo(Long id) {
        return String.format("Pedido #%05d", id);
    }

    @Transactional(readOnly = true)
    public List<PedidoResumen> pendientes(String correo) {
        return pedidos.findByClienteCorreoAndEstadoNotOrderByIdAsc(correo, EstadoPedido.RECOGIDO)
                .stream()
                .map(p -> new PedidoResumen(p.getId(), codigo(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoDetalle detalle(Long id, String correo) {
        PedidoEntity p = pedidos.findByIdAndClienteCorreo(id, correo)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));
        return new PedidoDetalle(
                p.getId(),
                codigo(p.getId()),
                p.getCliente().getNombre() + " " + p.getCliente().getApellidos(),
                p.getCliente().getCorreo(),
                p.getTarjeta() == null ? "Tarjeta" : "Tarjeta " + p.getTarjeta(),
                p.getTotal(),
                p.getEstado().getEtiqueta(),
                p.getEstado().ordinal());
    }
}