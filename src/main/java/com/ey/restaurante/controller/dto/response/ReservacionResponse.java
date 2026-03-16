package com.ey.restaurante.controller.dto.response;

import com.ey.restaurante.model.entity.EstadoReservacion;
import com.ey.restaurante.model.entity.Reservacion;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "Response con los datos de una reservación")
public record ReservacionResponse(

        @Schema(description = "ID único de la reservación", example = "1")
        Long id,

        @Schema(description = "Nombre del cliente", example = "Juan Pérez")
        String nombreCliente,

        @Schema(description = "Teléfono de contacto", example = "+51999888777")
        String telefono,

        @Schema(description = "Número de personas", example = "4")
        Integer numeroPersonas,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Fecha de la reservación", example = "2024-12-25")
        LocalDate fechaReservacion,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Hora de la reservación", example = "10:30")
        LocalTime horaReservacion,

        @Schema(description = "Estado de la reservación", example = "ACTIVA")
        EstadoReservacion estado,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Fecha y hora de creación", example = "2024-12-20 15:30:00")
        LocalDateTime fechaCreacion
) {

    public static ReservacionResponse fromEntity(Reservacion reservacion) {
        return new ReservacionResponse(
                reservacion.getId(),
                reservacion.getNombreCliente(),
                reservacion.getTelefono(),
                reservacion.getNumeroPersonas(),
                reservacion.getFechaReservacion(),
                reservacion.getHoraReservacion(),
                reservacion.getEstado(),
                reservacion.getFechaCreacion()
        );
    }
}
