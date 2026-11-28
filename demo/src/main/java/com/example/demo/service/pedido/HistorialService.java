package com.example.demo.service.pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.Entity.pedido.PedidoItemEntity;
import com.example.demo.dto.BoletaDetalle;
import com.example.demo.dto.HistorialBoleta;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.pedido.EstadoPedido;
import com.example.demo.repository.PedidoRepository;

@Service
public class HistorialService {

    private final PedidoRepository pedidos;

    public HistorialService(PedidoRepository pedidos) {
        this.pedidos = pedidos;
    }

    @Transactional(readOnly = true)
    public List<HistorialBoleta> listar(String correo, LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? LocalDateTime.now().plusDays(1) : hasta.plusDays(1).atStartOfDay();

        return pedidos.historial(correo, EstadoPedido.RECOGIDO, inicio, fin)
                .stream()
                .map(p -> new HistorialBoleta(
                        p.getId(),
                        PedidoService.codigo(p.getId()),
                        p.getFecha(),
                        p.getItems().stream().mapToInt(PedidoItemEntity::getCantidad).sum(),
                        PedidoService.metodoPago(p),
                        p.getTotal()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BoletaDetalle boleta(Long id, String correo) {
        PedidoEntity p = pedidos.findByIdAndClienteCorreo(id, correo)
                .filter(pedido -> pedido.getEstado() == EstadoPedido.RECOGIDO)
                .orElseThrow(() -> new BusinessException("La boleta no existe o el pedido aún no fue recogido"));

        List<BoletaDetalle.Item> items = p.getItems().stream()
                .map(i -> new BoletaDetalle.Item(
                        i.getNombre(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))))
                .toList();

        ClienteEntity c = p.getCliente();
        return new BoletaDetalle(
                PedidoService.codigo(p.getId()),
                c.getNombre() + " " + c.getApellidos(),
                c.getDni(),
                c.getCorreo(),
                p.getFecha(),
                PedidoService.metodoPago(p),
                items,
                p.getTotal());
    }
}