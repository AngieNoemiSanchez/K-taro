package com.example.demo.service.catalogo.impl;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.Entity.catalogo.PromocionEntity;
import com.example.demo.dto.PromotionCard;
import com.example.demo.dto.admin.PromotionForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.PromocionMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Promocion;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.PromocionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl implements PromocionService {

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
        e.setImagen(images.store(form.getImage()).orElse(null));
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
        Optional<String> nueva = images.store(form.getImage());
        if (nueva.isPresent()) {
            images.delete(e.getImagen());
            e.setImagen(nueva.get());
        }
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionCard> listarTarjetasVisibles() {
        return repo.findByEstadoOrderByIdAsc(Estado.ACTIVO).stream()
                .map(this::toCard)
                .flatMap(Optional::stream)
                .toList();
    }

    /** Valida productos y precio, y copia los datos del formulario a la entidad. */
    private void aplicar(PromocionEntity e, PromotionForm form) {
        List<Long> ids = form.getProductIds().stream().distinct().toList();
        Map<Long, ProductoEntity> encontrados = productos.findAllById(ids).stream()
                .collect(Collectors.toMap(ProductoEntity::getId, p -> p));
        if (ids.isEmpty() || encontrados.size() != ids.size()) {
            throw new BusinessException("Alguno de los productos no existe");
        }
        BigDecimal total = encontrados.values().stream()
                .map(ProductoEntity::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (form.getPrecio().compareTo(total) >= 0) {
            throw new BusinessException("El precio de la promoción debe ser menor a S/ " + total);
        }
        e.setNombre(form.getNombre().trim());
        e.setDescripcion(form.getDescripcion().trim());
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
                items.stream().map(ProductoEntity::getNombre).toList(), stock));
    }
}