package com.example.demo.service.catalogo;

import com.example.demo.dto.admin.ProductForm;
import com.example.demo.model.catalogo.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> listar();
    Optional<Producto> buscarPorId(Long id);
    Producto crear(ProductForm form);
    Producto actualizar(Long id, ProductForm form);
    Producto agregarStock(Long id, int cantidad);
}