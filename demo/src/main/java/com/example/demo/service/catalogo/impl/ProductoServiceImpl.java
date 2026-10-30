package com.example.demo.service.catalogo.impl;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.dto.admin.ProductForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repo;
    private final ProductoMapper mapper;
    private final ImageStorageService images;

    public ProductoServiceImpl(ProductoRepository repo, ProductoMapper mapper, ImageStorageService images) {
        this.repo = repo;
        this.mapper = mapper;
        this.images = images;
    }

    @Override
    public List<Producto> listar() {
        return repo.findAllByOrderByNombreAsc().stream().map(mapper::toModel).toList();
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return repo.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public Producto crear(ProductForm form) {
        if (repo.existsByNombreIgnoreCase(form.getName().trim())) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }
        ProductoEntity e = mapper.toEntity(form);
        e.setImagen(images.store(form.getImage()).orElse(null));
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional
    public Producto actualizar(Long id, ProductForm form) {
        ProductoEntity e = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        if (repo.existsByNombreIgnoreCaseAndIdNot(form.getName().trim(), id)) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }
        mapper.updateEntityFromForm(form, e);
        Optional<String> nueva = images.store(form.getImage());
        if (nueva.isPresent()) {
            images.delete(e.getImagen());
            e.setImagen(nueva.get());
        }
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional
    public Producto agregarStock(Long id, int cantidad) {
        ProductoEntity e = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        e.setStock(e.getStock() + cantidad);
        return mapper.toModel(repo.save(e));
    }
}