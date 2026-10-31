package com.example.demo.repository;

import com.example.demo.Entity.catalogo.PromocionEntity;
import com.example.demo.model.catalogo.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocionRepository extends JpaRepository<PromocionEntity, Long> {
    List<PromocionEntity> findAllByOrderByIdAsc();
    List<PromocionEntity> findByEstadoOrderByIdAsc(Estado estado);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}