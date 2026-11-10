package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.model.pedido.EstadoPedido;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByClienteCorreoAndEstadoNotOrderByIdAsc(String correo, EstadoPedido estado);

    Optional<PedidoEntity> findByIdAndClienteCorreo(Long id, String correo);
}