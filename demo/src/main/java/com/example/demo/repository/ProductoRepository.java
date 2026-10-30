package com.example.demo.repository;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.model.catalogo.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findAllByOrderByNombreAsc();
    List<ProductoEntity> findByTipoProductoOrderByNombreAsc(TipoProducto tipo);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}