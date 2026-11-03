package com.example.demo.service.pedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.Entity.pedido.CarritoEntity;
import com.example.demo.Entity.pedido.CarritoItemEntity;
import com.example.demo.dto.CarritoItemDto;
import com.example.demo.dto.CarritoResponse;
import com.example.demo.dto.OfferCard;
import com.example.demo.dto.PromotionCard;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.pedido.TipoItemCarrito;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import com.example.demo.service.CustomerCatalogService;
import com.example.demo.service.catalogo.PromocionService;

@Service
public class CarritoService {

    private static final String PREFIJO_COMBO = "promo-";
    private static final String PREFIJO_OFERTA = "oferta-";
    private static final int MAX_UNIDADES = 99;

    private final CarritoRepository carritos;
    private final ClienteRepository clientes;
    private final ProductoRepository productos;
    private final PromocionRepository promociones;
    private final CustomerCatalogService catalog;
    private final PromocionService promocionService;

    public CarritoService(CarritoRepository carritos, ClienteRepository clientes,
                          ProductoRepository productos, PromocionRepository promociones,
                          CustomerCatalogService catalog, PromocionService promocionService) {
        this.carritos = carritos;
        this.clientes = clientes;
        this.productos = productos;
        this.promociones = promociones;
        this.catalog = catalog;
        this.promocionService = promocionService;
    }

    @Transactional
    public CarritoResponse obtener(String correo) {
        return armar(carritoDe(correo));
    }

    @Transactional
    public CarritoResponse agregar(String correo, String cartId, int cantidad) {
        CarritoEntity carrito = carritoDe(correo);
        TipoItemCarrito tipo = tipoDe(cartId);
        Long ref = referenciaDe(cartId);

        int actual = carrito.buscar(tipo, ref).map(CarritoItemEntity::getCantidad).orElse(0);
        int nueva = actual + cantidad;
        validar(cartId, nueva);

        carrito.buscar(tipo, ref).orElseGet(() -> carrito.agregarItem(tipo, ref)).setCantidad(nueva);
        return armar(carrito);
    }

    @Transactional
    public CarritoResponse fijarCantidad(String correo, String cartId, int cantidad) {
        CarritoEntity carrito = carritoDe(correo);
        TipoItemCarrito tipo = tipoDe(cartId);
        Long ref = referenciaDe(cartId);

        if (cantidad <= 0) {
            carrito.getItems().removeIf(i -> i.getTipoItem() == tipo && i.getIdReferencia().equals(ref));
        } else {
            validar(cartId, cantidad);
            carrito.buscar(tipo, ref).orElseGet(() -> carrito.agregarItem(tipo, ref)).setCantidad(cantidad);
        }
        return armar(carrito);
    }

    @Transactional
    public CarritoResponse vaciar(String correo) {
        CarritoEntity carrito = carritoDe(correo);
        carrito.getItems().clear();
        return armar(carrito);
    }


    private CarritoEntity carritoDe(String correo) {
        ClienteEntity cliente = clientes.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Inicia sesión para usar el carrito"));
        return carritos.findByClienteId(cliente.getId()).orElseGet(() -> {
            CarritoEntity nuevo = new CarritoEntity();
            nuevo.setCliente(cliente);
            return carritos.save(nuevo);
        });
    }

    private void validar(String cartId, int cantidad) {
        if (cantidad > MAX_UNIDADES) {
            throw new BusinessException("La cantidad máxima por producto es " + MAX_UNIDADES);
        }
        int disponibles = disponibles(cartId);
        if (cantidad > disponibles) {
            throw new BusinessException(disponibles == 0
                    ? "El producto está agotado o ya no está disponible"
                    : "Solo quedan " + disponibles + " unidades");
        }
    }

    private int disponibles(String cartId) {
        if (cartId.startsWith(PREFIJO_COMBO)) {
            return promocionService.listarTarjetasVisibles().stream()
                    .filter(p -> p.id().equals(cartId))
                    .mapToInt(PromotionCard::availableUnits).findFirst().orElse(0);
        }
        if (cartId.startsWith(PREFIJO_OFERTA)) {
            return promocionService.listarOfertasVisibles().stream()
                    .filter(o -> o.id().equals(cartId))
                    .mapToInt(OfferCard::availableUnits).findFirst().orElse(0);
        }
        return catalog.availableUnits(cartId);
    }

    private CarritoResponse armar(CarritoEntity carrito) {
        List<CarritoItemDto> items = carrito.getItems().stream()
                .map(this::aDto)
                .flatMap(Optional::stream)
                .toList();
        BigDecimal total = items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CarritoResponse(items, total);
    }

    private Optional<CarritoItemDto> aDto(CarritoItemEntity item) {
        int cant = item.getCantidad();
        String id = item.getCartId();
        return switch (item.getTipoItem()) {
            case PRODUCTO -> productos.findById(item.getIdReferencia())
                    .map(p -> new CarritoItemDto(id, p.getNombre(), p.getPrecio(), cant,
                            p.getTipoProducto().name()));
            case COMBO, OFERTA -> promociones.findById(item.getIdReferencia())
                    .map(p -> new CarritoItemDto(id, p.getNombre(), p.getPrecio(), cant,
                            item.getTipoItem().name()));
        };
    }

    private static TipoItemCarrito tipoDe(String id) {
        if (id.startsWith(PREFIJO_COMBO)) return TipoItemCarrito.COMBO;
        if (id.startsWith(PREFIJO_OFERTA)) return TipoItemCarrito.OFERTA;
        return TipoItemCarrito.PRODUCTO;
    }

    private static Long referenciaDe(String id) {
        String numero = id.startsWith(PREFIJO_COMBO) ? id.substring(PREFIJO_COMBO.length())
                : id.startsWith(PREFIJO_OFERTA) ? id.substring(PREFIJO_OFERTA.length())
                : id;
        try {
            return Long.valueOf(numero.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("Producto inválido");
        }
    }
}