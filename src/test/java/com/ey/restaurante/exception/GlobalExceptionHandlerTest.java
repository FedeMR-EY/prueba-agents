package com.ey.restaurante.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/reservaciones");
    }

    @Test
    @DisplayName("Debe manejar ReservacionNotFoundApiException")
    void debeManejarReservacionNotFound() {
        ReservacionNotFoundApiException ex = new ReservacionNotFoundApiException(1L);

        ResponseEntity<ApiError> response = handler.handleApiException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("RESERVACION_NOT_FOUND");
        assertThat(response.getBody().getMessage()).contains("1");
    }

    @Test
    @DisplayName("Debe manejar LimiteReservacionesApiException")
    void debeManejarLimiteReservaciones() {
        LimiteReservacionesApiException ex = new LimiteReservacionesApiException(LocalDate.of(2024, 12, 25));

        ResponseEntity<ApiError> response = handler.handleApiException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("LIMITE_RESERVACIONES_EXCEDIDO");
    }

    @Test
    @DisplayName("Debe manejar HorarioInvalidoApiException")
    void debeManejarHorarioInvalido() {
        HorarioInvalidoApiException ex = new HorarioInvalidoApiException(LocalTime.of(8, 0));

        ResponseEntity<ApiError> response = handler.handleApiException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("HORARIO_INVALIDO");
        assertThat(response.getBody().getMessage()).contains("08:00");
    }

    @Test
    @DisplayName("Debe manejar excepciones genéricas")
    void debeManejarExcepcionGenerica() {
        Exception ex = new RuntimeException("Error inesperado");

        ResponseEntity<ApiError> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
    }
}
