package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.model.pedido.EstadoPedido;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByClienteCorreoAndEstadoNotOrderByIdAsc(String correo, EstadoPedido estado);

    Optional<PedidoEntity> findByIdAndClienteCorreo(Long id, String correo);

    @EntityGraph(attributePaths = "cliente")
    List<PedidoEntity> findByEstadoNotOrderByIdAsc(EstadoPedido estado);

    @EntityGraph(attributePaths = "cliente")
    List<PedidoEntity> findByEstadoOrderByIdDesc(EstadoPedido estado);

    @Query("select distinct p from PedidoEntity p left join fetch p.items where p.fecha >= :desde and p.fecha < :hasta")
    List<PedidoEntity> findConItemsEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select min(p.fecha) from PedidoEntity p")
    LocalDateTime primeraFecha();
}