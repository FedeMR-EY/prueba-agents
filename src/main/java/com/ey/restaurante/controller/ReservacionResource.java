package com.ey.restaurante.controller;

import com.ey.restaurante.controller.dto.request.CreateReservacionRequest;
import com.ey.restaurante.controller.dto.response.ReservacionResponse;
import com.ey.restaurante.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reservaciones", description = "API para gestión de reservaciones del restaurante")
@RequestMapping("/api/v1/reservaciones")
public interface ReservacionResource {

    @Operation(summary = "Crear nueva reservación",
               description = "Crea una nueva reservación. Máximo 5 reservaciones activas por día. Horario: 9:00 AM - 12:00 PM")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservación creada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservacionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o horario fuera del permitido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Límite de reservaciones alcanzado para el día",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    ResponseEntity<ReservacionResponse> crearReservacion(
            @Valid @RequestBody CreateReservacionRequest request);

    @Operation(summary = "Listar todas las reservaciones",
               description = "Obtiene la lista de todas las reservaciones (activas y canceladas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservaciones obtenida exitosamente")
    })
    @GetMapping
    ResponseEntity<List<ReservacionResponse>> listarReservaciones();

    @Operation(summary = "Obtener reservación por ID",
               description = "Obtiene los detalles de una reservación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación encontrada",
                    content = @Content(schema = @Schema(implementation = ReservacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<ReservacionResponse> obtenerReservacionPorId(
            @Parameter(description = "ID de la reservación", required = true)
            @PathVariable Long id);

    @Operation(summary = "Cancelar reservación",
               description = "Cancela una reservación existente (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación cancelada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<ReservacionResponse> cancelarReservacion(
            @Parameter(description = "ID de la reservación a cancelar", required = true)
            @PathVariable Long id);
}
