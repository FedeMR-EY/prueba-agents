package com.ey.restaurante.repository;

import com.ey.restaurante.model.entity.EstadoReservacion;
import com.ey.restaurante.model.entity.Reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {

    @Query("SELECT COUNT(r) FROM Reservacion r WHERE r.fechaReservacion = :fecha AND r.estado = :estado")
    long countByFechaReservacionAndEstado(@Param("fecha") LocalDate fecha, @Param("estado") EstadoReservacion estado);

    default long countReservacionesActivasPorFecha(LocalDate fecha) {
        return countByFechaReservacionAndEstado(fecha, EstadoReservacion.ACTIVA);
    }
}
