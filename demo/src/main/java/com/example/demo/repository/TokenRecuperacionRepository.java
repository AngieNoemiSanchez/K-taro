package com.example.demo.repository;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.Entity.cliente.TokenRecuperacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacionEntity, Long> {
    Optional<TokenRecuperacionEntity> findByTokenHash(String tokenHash);
    void deleteByCliente(ClienteEntity cliente);
}