package com.ey.restaurante.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Request para crear una nueva reservación")
public record CreateReservacionRequest(

        @NotBlank(message = "El nombre del cliente es requerido")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Schema(description = "Nombre del cliente", example = "Juan Pérez")
        String nombreCliente,

        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
        @Schema(description = "Teléfono de contacto", example = "+51999888777")
        String telefono,

        @NotNull(message = "El número de personas es requerido")
        @Min(value = 1, message = "Debe haber al menos 1 persona")
        @Max(value = 20, message = "Máximo 20 personas por reservación")
        @Schema(description = "Número de personas", example = "4")
        Integer numeroPersonas,

        @NotNull(message = "La fecha de reservación es requerida")
        @FutureOrPresent(message = "La fecha de reservación debe ser hoy o en el futuro")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Fecha de la reservación", example = "2024-12-25")
        LocalDate fechaReservacion,

        @NotNull(message = "La hora de reservación es requerida")
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Hora de la reservación (formato 24h)", example = "10:30")
        LocalTime horaReservacion
) {
}
