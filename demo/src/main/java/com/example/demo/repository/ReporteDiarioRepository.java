package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.admin.ReporteDiarioEntity;

public interface ReporteDiarioRepository extends JpaRepository<ReporteDiarioEntity, Long> {

    List<ReporteDiarioEntity> findByFechaBetweenOrderByFechaAsc(LocalDate desde, LocalDate hasta);
}