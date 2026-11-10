package com.example.demo.service.pedido;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.dto.admin.PedidoAdminFila;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.pedido.EstadoPedido;
import com.example.demo.repository.PedidoRepository;

@Service
public class AdminPedidoService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidoRepository pedidos;

    public AdminPedidoService(PedidoRepository pedidos) {
        this.pedidos = pedidos;
    }

    @Transactional(readOnly = true)
    public List<PedidoAdminFila> activos() {
        return pedidos.findByEstadoNotOrderByIdAsc(EstadoPedido.RECOGIDO)
                .stream().map(this::aFila).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoAdminFila> registrados() {
        return pedidos.findByEstadoOrderByIdDesc(EstadoPedido.RECOGIDO)
                .stream().map(this::aFila).toList();
    }

    @Transactional
    public PedidoAdminFila cambiarEstado(Long id, EstadoPedido nuevo) {
        PedidoEntity pedido = pedidos.findById(id)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));

        EstadoPedido actual = pedido.getEstado();
        EstadoPedido siguiente = actual.siguiente();
        String codigo = PedidoService.codigo(pedido.getId());

        if (siguiente == null) {
            throw new BusinessException(codigo + " ya fue recogido y su estado no se puede modificar.");
        }
        if (nuevo.ordinal() <= actual.ordinal()) {
            throw new BusinessException(codigo + " ya está en \"" + actual.getEtiqueta()
                    + "\". No se puede volver a un estado anterior.");
        }
        if (nuevo != siguiente) {
            throw new BusinessException(codigo + " solo puede pasar a \"" + siguiente.getEtiqueta() + "\".");
        }

        pedido.setEstado(nuevo);
        return aFila(pedido);
    }

    private PedidoAdminFila aFila(PedidoEntity p) {
        EstadoPedido siguiente = p.getEstado().siguiente();
        return new PedidoAdminFila(
                p.getId(),
                PedidoService.codigo(p.getId()),
                p.getCliente().getNombre() + " " + p.getCliente().getApellidos(),
                FORMATO_FECHA.format(p.getFecha()),
                p.getEstado().name(),
                p.getEstado().getEtiqueta(),
                siguiente == null ? null : siguiente.name(),
                siguiente == null ? null : siguiente.getEtiqueta());
    }
}