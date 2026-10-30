package com.example.demo.repository;

import com.example.demo.Entity.admin.AdministradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<AdministradorEntity, Long> {
    Optional<AdministradorEntity> findByCorreoIgnoreCase(String correo);
}