package com.example.demo.service.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.model.AdminProduct;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class ProductAdminService {

    private final ProductoRepository repo;

    public ProductAdminService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<AdminProduct> findAll() {
        return repo.findAllByOrderByNombreAsc().stream()
                .map(ProductAdminService::toAdminProduct)
                .toList();
    }

    public List<AdminProduct> findAllByCreation() {
        return repo.findAll(Sort.by("id")).stream()
                .map(ProductAdminService::toAdminProduct)
                .toList();
    }

    public Optional<AdminProduct> findById(String id) {
        return parseId(id)
                .flatMap(repo::findById)
                .map(ProductAdminService::toAdminProduct);
    }

    private static Optional<Long> parseId(String id) {
        try {
            return Optional.of(Long.valueOf(id.trim()));
        } catch (NumberFormatException | NullPointerException e) {
            return Optional.empty();
        }
    }

    private static AdminProduct toAdminProduct(ProductoEntity e) {
        return new AdminProduct(
                String.valueOf(e.getId()),
                e.getNombre(),
                e.getDescripcion(),
                categoria(e),
                e.getPrecio(),
                null,
                null,
                e.getStock(),
                e.getEstado() == Estado.ACTIVO,
                e.getImagen(),
                List.of());
    }

    private static String categoria(ProductoEntity e) {
        return switch (e.getTipoProducto()) {
            case SNACK -> "snacks";
            case BEBIDA -> "bebidas";
            case RAMEN -> "ramen";
            case OTROS -> "otros";
        };
    }
}