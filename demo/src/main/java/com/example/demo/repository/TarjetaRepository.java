package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.pedido.TarjetaEntity;
import com.example.demo.model.pedido.EstadoTarjeta;

import jakarta.persistence.LockModeType;

public interface TarjetaRepository extends JpaRepository<TarjetaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TarjetaEntity t where t.numero = :numero")
    Optional<TarjetaEntity> findByNumeroForUpdate(@Param("numero") String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TarjetaEntity t where t.id = :id")
    Optional<TarjetaEntity> findByIdForUpdate(@Param("id") Long id);

    List<TarjetaEntity> findByCliente_IdAndEstadoOrderByIdAsc(Long clienteId, EstadoTarjeta estado);

    long countByCliente_IdAndEstado(Long clienteId, EstadoTarjeta estado);
}