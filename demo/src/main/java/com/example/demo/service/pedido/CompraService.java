package com.example.demo.service.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.Entity.catalogo.PromocionEntity;
import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.Entity.pedido.TarjetaEntity;
import com.example.demo.dto.CompraRequest;
import com.example.demo.dto.CompraResponse;
import com.example.demo.dto.cliente.MetodoDePago;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.TarjetaMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.TipoPromocion;
import com.example.demo.model.pedido.Tarjeta;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import com.example.demo.repository.TarjetaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Service
public class CompraService {

    private static final ZoneId ZONA_TIENDA = ZoneId.of("America/Lima");
    private static final String PREFIJO_COMBO = "promo-";
    private static final String PREFIJO_OFERTA = "oferta-";
    private static final int MAX_UNIDADES_POR_ITEM = 99;

    private static final String TARJETA_INVALIDA = "Los datos de la tarjeta no son válidos";

    @PersistenceContext
    private EntityManager em;

    private final TarjetaRepository tarjetas;
    private final TarjetaMapper tarjetaMapper;
    private final ProductoRepository productos;
    private final PromocionRepository promociones;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clientes;
    private final PedidoRepository pedidos;

    public CompraService(TarjetaRepository tarjetas, TarjetaMapper tarjetaMapper,
                         ProductoRepository productos, PromocionRepository promociones,
                         PasswordEncoder passwordEncoder,
                         ClienteRepository clientes, PedidoRepository pedidos) {
        this.tarjetas = tarjetas;
        this.tarjetaMapper = tarjetaMapper;
        this.productos = productos;
        this.promociones = promociones;
        this.passwordEncoder = passwordEncoder;
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    @Transactional
    public CompraResponse confirmar(CompraRequest request, String correo) {
        ClienteEntity cliente = clientes.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Inicia sesión para comprar"));

        MetodoDePago datos = request.tarjeta();

        TarjetaEntity tarjeta = tarjetas.findByNumeroForUpdate(datos.getNumeroTarjeta())
                .orElseThrow(() -> new BusinessException(TARJETA_INVALIDA));
        validarTarjeta(tarjeta, datos);

        Map<String, Integer> cantidades = agruparItems(request);
        Map<Long, Integer> unidadesPorProducto = new TreeMap<>();
        BigDecimal total = BigDecimal.ZERO;

        record Linea(String nombre, int cantidad, BigDecimal precio) {}
        List<Linea> lineas = new ArrayList<>();

        for (Map.Entry<String, Integer> item : cantidades.entrySet()) {
            String id = item.getKey();
            int cantidad = item.getValue();
            BigDecimal precioUnitario;
            String nombre;

            if (id.startsWith(PREFIJO_COMBO)) {
                PromocionEntity promo = promocionVigente(id, PREFIJO_COMBO, TipoPromocion.COMBO);
                precioUnitario = promo.getPrecio();
                nombre = promo.getNombre();
                promo.getProductos().forEach(p -> unidadesPorProducto.merge(p.getId(), cantidad, Integer::sum));
            } else if (id.startsWith(PREFIJO_OFERTA)) {
                PromocionEntity oferta = promocionVigente(id, PREFIJO_OFERTA, TipoPromocion.OFERTA);
                ProductoEntity producto = oferta.getProductos().stream().findFirst()
                        .orElseThrow(() -> new BusinessException("La oferta ya no está disponible"));
                if (oferta.getPrecio().compareTo(producto.getPrecio()) >= 0) {
                    throw new BusinessException("La oferta ya no está disponible");
                }
                precioUnitario = oferta.getPrecio();
                nombre = producto.getNombre();
                unidadesPorProducto.merge(producto.getId(), cantidad, Integer::sum);
            } else {
                ProductoEntity producto = productos.findById(parseId(id))
                        .orElseThrow(() -> new BusinessException("Un producto del carrito ya no existe"));
                precioUnitario = producto.getPrecio();
                nombre = producto.getNombre();
                unidadesPorProducto.merge(producto.getId(), cantidad, Integer::sum);
            }
            lineas.add(new Linea(nombre, cantidad, precioUnitario));
            total = total.add(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
        }
        total = total.setScale(2, RoundingMode.HALF_UP);

        for (Map.Entry<Long, Integer> unidades : unidadesPorProducto.entrySet()) {
            ProductoEntity producto = em.find(ProductoEntity.class, unidades.getKey());
            if (producto == null) {
                throw new BusinessException("Un producto del carrito ya no existe");
            }
            em.refresh(producto, LockModeType.PESSIMISTIC_WRITE);
            if (producto.getEstado() != Estado.ACTIVO) {
                throw new BusinessException("«" + producto.getNombre() + "» ya no está disponible");
            }
            if (producto.getStock() < unidades.getValue()) {
                throw new BusinessException("Stock insuficiente de «" + producto.getNombre()
                        + "» (quedan " + producto.getStock() + ")");
            }
            producto.setStock(producto.getStock() - unidades.getValue());
        }

        if (tarjeta.getSaldo().compareTo(total) < 0) {
            throw new BusinessException("Saldo insuficiente en la tarjeta");
        }
        tarjeta.setSaldo(tarjeta.getSaldo().subtract(total));

        String enmascarada = tarjetaMapper.toModel(tarjeta).getNumeroEnmascarado();

        PedidoEntity pedido = new PedidoEntity();
        pedido.setCliente(cliente);
        pedido.setTotal(total);
        pedido.setTarjeta(enmascarada);
        lineas.forEach(l -> pedido.agregarItem(l.nombre(), l.cantidad(), l.precio()));
        pedidos.save(pedido);

        return new CompraResponse(total, enmascarada, pedido.getId());
    }

    private void validarTarjeta(TarjetaEntity entidad, MetodoDePago datos) {
        Tarjeta tarjeta = tarjetaMapper.toModel(entidad);

        boolean datosCorrectos =
                normalizar(tarjeta.getTitular()).equals(normalizar(datos.getTitular()))
                        && tarjeta.getFechaVencimiento()
                        .equals(tarjetaMapper.aFechaVencimiento(datos.getVencimiento()))
                        && passwordEncoder.matches(datos.getCvv(), tarjeta.getCvvHash());
        if (!datosCorrectos) {
            throw new BusinessException(TARJETA_INVALIDA);
        }
        if (!tarjeta.isActiva()) {
            throw new BusinessException("La tarjeta no está habilitada para pagar");
        }
        if (tarjeta.estaVencida(LocalDateTime.now(ZONA_TIENDA))) {
            throw new BusinessException("La tarjeta está vencida");
        }
    }

    private static String normalizar(String texto) {
        return texto.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    private static Map<String, Integer> agruparItems(CompraRequest request) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        request.items().forEach(i -> cantidades.merge(i.id().trim(), i.quantity(), Integer::sum));
        cantidades.forEach((id, cantidad) -> {
            if (cantidad > MAX_UNIDADES_POR_ITEM) {
                throw new BusinessException("La cantidad máxima por producto es " + MAX_UNIDADES_POR_ITEM);
            }
        });
        return cantidades;
    }

    private PromocionEntity promocionVigente(String id, String prefijo, TipoPromocion tipo) {
        PromocionEntity promo = promociones.findById(parseId(id.substring(prefijo.length())))
                .orElseThrow(() -> new BusinessException("Una promoción del carrito ya no existe"));
        if (promo.getTipoPromocion() != tipo || promo.getEstado() != Estado.ACTIVO
                || promo.getProductos().isEmpty()) {
            throw new BusinessException("«" + promo.getNombre() + "» ya no está disponible");
        }
        DayOfWeek hoy = LocalDate.now(ZONA_TIENDA).getDayOfWeek();
        if (!promo.getTiempo().disponibleEn(hoy)) {
            throw new BusinessException("«" + promo.getNombre() + "»: " + promo.getTiempo().getAviso());
        }
        Set<ProductoEntity> items = promo.getProductos();
        if (items.stream().anyMatch(p -> p.getEstado() != Estado.ACTIVO)) {
            throw new BusinessException("«" + promo.getNombre() + "» ya no está disponible");
        }
        return promo;
    }

    private static Long parseId(String id) {
        try {
            return Long.valueOf(id.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("Un producto del carrito no es válido");
        }
    }
}