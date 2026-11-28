package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.pedido.PedidoEntity;
import com.example.demo.model.pedido.EstadoPedido;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByClienteCorreoAndEstadoNotOrderByIdAsc(String correo, EstadoPedido estado);

    Optional<PedidoEntity> findByIdAndClienteCorreo(Long id, String correo);

    @EntityGraph(attributePaths = "cliente")
    List<PedidoEntity> findByEstadoNotOrderByIdAsc(EstadoPedido estado);

    @EntityGraph(attributePaths = "cliente")
    List<PedidoEntity> findByEstadoOrderByIdDesc(EstadoPedido estado);

    List<PedidoEntity> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @EntityGraph(attributePaths = "items")
    @Query("select p from PedidoEntity p "
            + "where p.cliente.correo = :correo and p.estado = :estado "
            + "and p.fecha >= :desde and p.fecha < :hasta "
            + "order by p.fecha desc")
    List<PedidoEntity> historial(@Param("correo") String correo,
                                 @Param("estado") EstadoPedido estado,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);
}