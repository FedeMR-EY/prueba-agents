package com.ey.restaurante.service.business;

import com.ey.restaurante.controller.dto.request.CreateReservacionRequest;
import com.ey.restaurante.controller.dto.response.ReservacionResponse;
import com.ey.restaurante.exception.HorarioInvalidoApiException;
import com.ey.restaurante.exception.LimiteReservacionesApiException;
import com.ey.restaurante.exception.ReservacionNotFoundApiException;
import com.ey.restaurante.model.entity.EstadoReservacion;
import com.ey.restaurante.model.entity.Reservacion;
import com.ey.restaurante.service.ReservacionDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservacionBusinessService {

    private static final int LIMITE_RESERVACIONES_POR_DIA = 5;
    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(12, 0);

    private final ReservacionDatabaseService databaseService;

    @Transactional
    public ReservacionResponse crearReservacion(CreateReservacionRequest request) {
        validarHorario(request.horaReservacion());
        validarLimiteReservaciones(request.fechaReservacion());

        Reservacion reservacion = Reservacion.builder()
                .nombreCliente(request.nombreCliente())
                .telefono(request.telefono())
                .numeroPersonas(request.numeroPersonas())
                .fechaReservacion(request.fechaReservacion())
                .horaReservacion(request.horaReservacion())
                .estado(EstadoReservacion.ACTIVA)
                .build();

        Reservacion saved = databaseService.save(reservacion);
        return ReservacionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservacionResponse> listarReservaciones() {
        return databaseService.findAll().stream()
                .map(ReservacionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservacionResponse obtenerReservacionPorId(Long id) {
        Reservacion reservacion = databaseService.findById(id)
                .orElseThrow(() -> new ReservacionNotFoundApiException(id));
        return ReservacionResponse.fromEntity(reservacion);
    }

    @Transactional
    public ReservacionResponse cancelarReservacion(Long id) {
        Reservacion reservacion = databaseService.findById(id)
                .orElseThrow(() -> new ReservacionNotFoundApiException(id));

        reservacion.setEstado(EstadoReservacion.CANCELADA);
        Reservacion updated = databaseService.save(reservacion);
        return ReservacionResponse.fromEntity(updated);
    }

    private void validarHorario(LocalTime hora) {
        if (hora.isBefore(HORA_APERTURA) || hora.isAfter(HORA_CIERRE)) {
            throw new HorarioInvalidoApiException(hora);
        }
    }

    private void validarLimiteReservaciones(java.time.LocalDate fecha) {
        long reservacionesActivas = databaseService.countReservacionesActivasPorFecha(fecha);
        if (reservacionesActivas >= LIMITE_RESERVACIONES_POR_DIA) {
            throw new LimiteReservacionesApiException(fecha);
        }
    }
}
