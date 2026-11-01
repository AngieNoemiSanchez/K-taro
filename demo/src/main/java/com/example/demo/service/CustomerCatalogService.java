package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.TipoProducto;
import com.example.demo.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class CustomerCatalogService {

    private static final int RELATED_LIMIT = 3;

    private final ProductoRepository repo;
    private final ProductoMapper mapper;

    public CustomerCatalogService(ProductoRepository repo, ProductoMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<Producto> getFeaturedProducts() {
        return repo.findTop4ByEstadoOrderByIdDesc(Estado.ACTIVO).stream()
                .map(mapper::toModel)
                .toList();
    }

    public List<Producto> findByCategory(TipoProducto tipo) {
        return repo.findByTipoProductoAndEstadoOrderByIdAsc(tipo, Estado.ACTIVO).stream()
                .map(mapper::toModel)
                .toList();
    }

    public Optional<Producto> findVisibleById(String id) {
        return parseId(id)
                .flatMap(repo::findById)
                .filter(e -> e.getEstado() == Estado.ACTIVO)
                .map(mapper::toModel);
    }

    public List<Producto> findRelatedProducts(Producto producto) {
        return findByCategory(producto.getTipoProducto()).stream()
                .filter(r -> !r.getIdProducto().equals(producto.getIdProducto()))
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