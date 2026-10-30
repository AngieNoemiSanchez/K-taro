package com.example.demo.repository;

import com.example.demo.Entity.cliente.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository <ClienteEntity, Long> {
    Optional<ClienteEntity> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByDni(String dni);
}
