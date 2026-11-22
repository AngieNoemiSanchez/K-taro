package com.example.demo.service.catalogo.impl;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.dto.admin.ProductForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.ProductoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repo;
    private final ProductoMapper mapper;
    private final ImageStorageService images;
    private final Validator validator;

    public ProductoServiceImpl(ProductoRepository repo, ProductoMapper mapper,
                                ImageStorageService images, Validator validator) {
        this.repo = repo;
        this.mapper = mapper;
        this.images = images;
        this.validator = validator;
    }

    @Override
    public List<Producto> listar() {
        return repo.findAllByOrderByIdAsc().stream().map(mapper::toModel).toList();
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {

        return repo.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional
    public Producto crear(ProductForm form) {
        validar(form);
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
        validar(form);
        Optional<String> nueva = images.store(form.getImage());
        AtomicReference<String> imagenAnterior = new AtomicReference<>();
        if (nueva.isPresent()) {
            String imagenNueva = nueva.get();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_COMMITTED) {
                        images.delete(imagenAnterior.get());
                    } else {
                        images.delete(imagenNueva);
                    }
                }
            });
        }
        ProductoEntity e = repo.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        Integer stockActual = e.getStock();
        if (stockActual == null || stockActual < 0 || stockActual > Producto.MAX_STOCK) {
            throw new BusinessException("El stock actual del producto no es válido.");
        }
        if (repo.existsByNombreIgnoreCaseAndIdNot(form.getName().trim(), id)) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }
        mapper.updateEntityFromForm(form, e);
        if (nueva.isPresent()) {
            imagenAnterior.set(e.getImagen());
            e.setImagen(nueva.get());
        }
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional
    public Producto agregarStock(Long id, int cantidad) {
        if (cantidad < 1) {
            throw new BusinessException("La cantidad a agregar debe ser un número entero mayor a 0.");
        }
        ProductoEntity e = repo.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        Integer stockActual = e.getStock();
        if (stockActual == null || stockActual < 0 || stockActual > Producto.MAX_STOCK) {
            throw new BusinessException("El stock actual del producto no es válido.");
        }
        if (cantidad > Producto.MAX_STOCK - stockActual) {
            throw new BusinessException("El stock total no puede superar 100 unidades.");
        }
        e.setStock(stockActual + cantidad);
        return mapper.toModel(repo.save(e));
    }

    @Override
    @Transactional
    public Producto retirarStock(Long id, int cantidad) {
        if (cantidad < 1) {
            throw new BusinessException("La cantidad a retirar debe ser un número entero mayor a 0.");
        }
        ProductoEntity e = repo.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        Integer stockActual = e.getStock();
        if (stockActual == null || stockActual < 0 || stockActual > Producto.MAX_STOCK) {
            throw new BusinessException("El stock actual del producto no es válido.");
        }
        if (cantidad > stockActual) {
            throw new BusinessException("El stock no puede quedar en negativo.");
        }
        e.setStock(stockActual - cantidad);
        return mapper.toModel(repo.save(e));
    }

    private void validar(ProductForm form) {
        if (form == null) {
            throw new BusinessException("Los datos del producto son obligatorios.");
        }
        Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
        if (!violations.isEmpty()) {
            throw new BusinessException(violations.iterator().next().getMessage());
        }
    }
}
