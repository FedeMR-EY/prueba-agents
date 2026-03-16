package com.ey.restaurante.controller;

import com.ey.restaurante.controller.dto.request.CreateReservacionRequest;
import com.ey.restaurante.controller.dto.response.ReservacionResponse;
import com.ey.restaurante.exception.GlobalExceptionHandler;
import com.ey.restaurante.exception.HorarioInvalidoApiException;
import com.ey.restaurante.exception.LimiteReservacionesApiException;
import com.ey.restaurante.exception.ReservacionNotFoundApiException;
import com.ey.restaurante.model.entity.EstadoReservacion;
import com.ey.restaurante.service.business.ReservacionBusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservacionBusinessService businessService;

    @InjectMocks
    private ReservacionController controller;

    private ObjectMapper objectMapper;
    private ReservacionResponse reservacionResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        reservacionResponse = new ReservacionResponse(
                1L,
                "Juan Pérez",
                "+51999888777",
                4,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 30),
                EstadoReservacion.ACTIVA,
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /api/v1/reservaciones")
    class CrearReservacion {

        @Test
        @DisplayName("Debe crear reservación exitosamente")
        void debeCrearReservacion() throws Exception {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(10, 30)
            );

            when(businessService.crearReservacion(any())).thenReturn(reservacionResponse);

            mockMvc.perform(post("/api/v1/reservaciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"))
                    .andExpect(jsonPath("$.estado").value("ACTIVA"));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando horario es inválido")
        void debeRetornar400HorarioInvalido() throws Exception {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(8, 0)
            );

            when(businessService.crearReservacion(any()))
                    .thenThrow(new HorarioInvalidoApiException(LocalTime.of(8, 0)));

            mockMvc.perform(post("/api/v1/reservaciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("HORARIO_INVALIDO"));
        }

        @Test
        @DisplayName("Debe retornar 409 cuando se excede límite de reservaciones")
        void debeRetornar409LimiteExcedido() throws Exception {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(10, 0)
            );

            when(businessService.crearReservacion(any()))
                    .thenThrow(new LimiteReservacionesApiException(LocalDate.now().plusDays(1)));

            mockMvc.perform(post("/api/v1/reservaciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value("LIMITE_RESERVACIONES_EXCEDIDO"));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando faltan campos requeridos")
        void debeRetornar400CamposFaltantes() throws Exception {
            String invalidRequest = "{\"telefono\": \"+51999888777\"}";

            mockMvc.perform(post("/api/v1/reservaciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/reservaciones")
    class ListarReservaciones {

        @Test
        @DisplayName("Debe listar todas las reservaciones")
        void debeListarReservaciones() throws Exception {
            when(businessService.listarReservaciones()).thenReturn(List.of(reservacionResponse));

            mockMvc.perform(get("/api/v1/reservaciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].nombreCliente").value("Juan Pérez"));
        }

        @Test
        @DisplayName("Debe retornar lista vacía")
        void debeRetornarListaVacia() throws Exception {
            when(businessService.listarReservaciones()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/reservaciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/reservaciones/{id}")
    class ObtenerReservacionPorId {

        @Test
        @DisplayName("Debe obtener reservación por ID")
        void debeObtenerPorId() throws Exception {
            when(businessService.obtenerReservacionPorId(1L)).thenReturn(reservacionResponse);

            mockMvc.perform(get("/api/v1/reservaciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando no existe")
        void debeRetornar404NoExiste() throws Exception {
            when(businessService.obtenerReservacionPorId(999L))
                    .thenThrow(new ReservacionNotFoundApiException(999L));

            mockMvc.perform(get("/api/v1/reservaciones/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("RESERVACION_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/reservaciones/{id}")
    class CancelarReservacion {

        @Test
        @DisplayName("Debe cancelar reservación exitosamente")
        void debeCancelarReservacion() throws Exception {
            ReservacionResponse cancelada = new ReservacionResponse(
                    1L,
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(10, 30),
                    EstadoReservacion.CANCELADA,
                    LocalDateTime.now()
            );

            when(businessService.cancelarReservacion(1L)).thenReturn(cancelada);

            mockMvc.perform(delete("/api/v1/reservaciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value("CANCELADA"));
        }

        @Test
        @DisplayName("Debe retornar 404 al cancelar reservación inexistente")
        void debeRetornar404AlCancelarInexistente() throws Exception {
            when(businessService.cancelarReservacion(999L))
                    .thenThrow(new ReservacionNotFoundApiException(999L));

            mockMvc.perform(delete("/api/v1/reservaciones/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("RESERVACION_NOT_FOUND"));
        }
    }
}
