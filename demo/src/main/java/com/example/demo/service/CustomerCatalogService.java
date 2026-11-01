package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OfferCard;
import com.example.demo.dto.ProductCard;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.TipoProducto;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.catalogo.PromocionService;

@Service
@Transactional(readOnly = true)
public class CustomerCatalogService {

    private static final int RELATED_LIMIT = 3;
    private static final int FEATURED_LIMIT = 4;

    private final ProductoRepository repo;
    private final ProductoMapper mapper;
    private final PromocionService promociones;

    public CustomerCatalogService(ProductoRepository repo, ProductoMapper mapper,
                                  PromocionService promociones) {
        this.repo = repo;
        this.mapper = mapper;
        this.promociones = promociones;
    }

    public List<ProductCard> getFeaturedProducts() {
        Set<Long> enOferta = promociones.listarOfertasVisibles().stream()
                .map(OfferCard::productId)
                .collect(Collectors.toSet());
        return repo.findByEstadoOrderByIdDesc(Estado.ACTIVO).stream()
                .map(mapper::toModel)
                .filter(p -> !enOferta.contains(p.getIdProducto()))
                .limit(FEATURED_LIMIT)
                .map(p -> ProductCard.of(p, null))
                .toList();
    }

    public List<ProductCard> findByCategory(TipoProducto tipo) {
        Map<Long, OfferCard> ofertas = promociones.ofertasVigentesPorProducto();
        return repo.findByTipoProductoAndEstadoOrderByIdAsc(tipo, Estado.ACTIVO).stream()
                .map(mapper::toModel)
                .map(p -> ProductCard.of(p, ofertas.get(p.getIdProducto())))
                .toList();
    }

    public Optional<Producto> findVisibleById(String id) {
        return parseId(id)
                .flatMap(repo::findById)
                .filter(e -> e.getEstado() == Estado.ACTIVO)
                .map(mapper::toModel);
    }

    public Optional<ProductCard> findVisibleCardById(String id) {
        Map<Long, OfferCard> ofertas = promociones.ofertasVigentesPorProducto();
        return findVisibleById(id).map(p -> ProductCard.of(p, ofertas.get(p.getIdProducto())));
    }

    public List<ProductCard> findRelatedProducts(ProductCard producto) {
        return findByCategory(producto.tipoProducto()).stream()
                .filter(r -> !r.id().equals(producto.id()))
                .limit(RELATED_LIMIT)
                .toList();
    }

    public int availableUnits(String id) {
        return findVisibleById(id).map(Producto::getStock).orElse(0);
    }

    private static Optional<Long> parseId(String id) {
        try {
            return Optional.of(Long.valueOf(id.trim()));
        } catch (NumberFormatException | NullPointerException e) {
            return Optional.empty();
        }
    }
}