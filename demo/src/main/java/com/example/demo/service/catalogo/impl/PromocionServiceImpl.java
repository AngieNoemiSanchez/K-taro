package com.example.demo.service.catalogo.impl;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.Entity.catalogo.PromocionEntity;
import com.example.demo.dto.OfferCard;
import com.example.demo.dto.PromotionCard;
import com.example.demo.dto.admin.PromotionForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.PromocionMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Promocion;
import com.example.demo.model.catalogo.TipoPromocion;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.PromocionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl implements PromocionService {

    private static final ZoneId ZONA_TIENDA = ZoneId.of("America/Lima");

    private final PromocionRepository repo;
    private final ProductoRepository productos;
    private final PromocionMapper mapper;
    private final ImageStorageService images;

    public PromocionServiceImpl(PromocionRepository repo, ProductoRepository productos,
                                PromocionMapper mapper, ImageStorageService images) {
        this.repo = repo;
        this.productos = productos;
        this.mapper = mapper;
        this.images = images;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promocion> listar() {
        return repo.findAllByOrderByIdAsc().stream().map(mapper::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promocion> buscarPorId(Long id) {
        return repo.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public Promocion crear(PromotionForm form) {
        if (repo.existsByNombreIgnoreCase(form.getNombre().trim())) {
            throw new BusinessException("Ya existe una promoción con ese nombre");
        }
        PromocionEntity e = new PromocionEntity();
        aplicar(e, form);
        e.setImagen(imagenSegunTipo(form));
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional
    public Promocion actualizar(Long id, PromotionForm form) {
        PromocionEntity e = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Promoción no encontrada"));
        if (repo.existsByNombreIgnoreCaseAndIdNot(form.getNombre().trim(), id)) {
            throw new BusinessException("Ya existe una promoción con ese nombre");
        }
        aplicar(e, form);
        if (form.getTipoPromocion() == TipoPromocion.COMBO) {
            Optional<String> nueva = images.store(form.getImage());
            if (nueva.isPresent()) {
                images.delete(e.getImagen());
                e.setImagen(nueva.get());
            }
        }
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionCard> listarTarjetasVisibles() {
        return repo.findByEstadoOrderByIdAsc(Estado.ACTIVO).stream()
                .filter(e -> e.getTipoPromocion() == TipoPromocion.COMBO)
                .map(this::toCard)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferCard> listarOfertasVisibles() {
        return repo.findByEstadoOrderByIdAsc(Estado.ACTIVO).stream()
                .filter(e -> e.getTipoPromocion() == TipoPromocion.OFERTA)
                .map(this::toOffer)
                .flatMap(Optional::stream)
                .toList();
    }

    private String imagenSegunTipo(PromotionForm form) {
        if (form.getTipoPromocion() == TipoPromocion.OFERTA) {
            return null;
        }
        return images.store(form.getImage()).orElse(null);
    }

    private void aplicar(PromocionEntity e, PromotionForm form) {
        TipoPromocion tipo = form.getTipoPromocion();
        List<Long> ids = form.getProductIds().stream().distinct().toList();
        Map<Long, ProductoEntity> encontrados = productos.findAllById(ids).stream()
                .collect(Collectors.toMap(ProductoEntity::getId, p -> p));
        if (ids.isEmpty() || encontrados.size() != ids.size()) {
            throw new BusinessException("Alguno de los productos no existe");
        }
        if (tipo == TipoPromocion.OFERTA && ids.size() != 1) {
            throw new BusinessException("Una oferta debe tener exactamente un producto");
        }
        BigDecimal total = encontrados.values().stream()
                .map(ProductoEntity::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (form.getPrecio().compareTo(total) >= 0) {
            throw new BusinessException(tipo == TipoPromocion.OFERTA
                    ? "El precio de la oferta debe ser menor al precio del producto (S/ " + total + ")"
                    : "El precio de la promoción debe ser menor a S/ " + total);
        }

        String descripcion = form.getDescripcion() == null ? "" : form.getDescripcion().trim();
        if (descripcion.isEmpty()) {
            if (tipo == TipoPromocion.COMBO) {
                throw new BusinessException("Ingresa la descripción del combo");
            }
            descripcion = "Oferta en " + encontrados.get(ids.get(0)).getNombre();
        }

        e.setTipoPromocion(tipo);
        e.setNombre(form.getNombre().trim());
        e.setDescripcion(descripcion);
        e.setPrecio(form.getPrecio());
        e.setTiempo(form.getTiempo());
        e.setEstado(form.getEstado());
        e.getProductos().clear();
        ids.forEach(id -> e.getProductos().add(encontrados.get(id)));
    }

    private Optional<PromotionCard> toCard(PromocionEntity e) {
        Set<ProductoEntity> items = e.getProductos();
        if (items.isEmpty() || items.stream().anyMatch(p -> p.getEstado() != Estado.ACTIVO)) {
            return Optional.empty();
        }
        BigDecimal regular = items.stream().map(ProductoEntity::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int stock = items.stream().mapToInt(ProductoEntity::getStock).min().orElse(0);
        return Optional.of(new PromotionCard("promo-" + e.getId(), e.getNombre(), e.getDescripcion(),
                e.getImagen(), e.getPrecio(), regular, e.getTiempo().getEtiqueta(),
                items.stream().map(ProductoEntity::getNombre).toList(), stock,
                disponibleHoy(e), e.getTiempo().getAviso()));
    }

    private Optional<OfferCard> toOffer(PromocionEntity e) {
        Optional<ProductoEntity> primero = e.getProductos().stream().findFirst();
        if (primero.isEmpty() || primero.get().getEstado() != Estado.ACTIVO) {
            return Optional.empty();
        }
        ProductoEntity p = primero.get();
        BigDecimal regular = p.getPrecio();
        // Si luego se cambió el precio del producto y la oferta ya no rebaja, no se muestra.
        if (e.getPrecio().compareTo(regular) >= 0) {
            return Optional.empty();
        }
        int descuento = regular.subtract(e.getPrecio())
                .multiply(BigDecimal.valueOf(100))
                .divide(regular, 0, RoundingMode.HALF_UP)
                .intValue();
        return Optional.of(new OfferCard("oferta-" + e.getId(), p.getId(), p.getNombre(), p.getImagen(),
                regular, e.getPrecio(), descuento, e.getTiempo().getEtiqueta(), p.getStock(),
                disponibleHoy(e), e.getTiempo().getAviso()));
    }

    private static boolean disponibleHoy(PromocionEntity e) {
        DayOfWeek hoy = LocalDate.now(ZONA_TIENDA).getDayOfWeek();
        return e.getTiempo().disponibleEn(hoy);
    }
}