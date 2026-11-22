package com.example.demo.repository;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findAllByOrderByNombreAsc();
    List<ProductoEntity> findAllByOrderByIdAsc();
    List<ProductoEntity> findByTipoProductoOrderByNombreAsc(TipoProducto tipo);
    List<ProductoEntity> findByTipoProductoAndEstadoOrderByIdAsc(TipoProducto tipo, Estado estado);
    List<ProductoEntity> findTop4ByEstadoOrderByIdDesc(Estado estado);
    List<ProductoEntity> findByEstadoOrderByIdDesc(Estado estado);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductoEntity p where p.id = :id")
    Optional<ProductoEntity> findByIdForUpdate(@Param("id") Long id);
}