package com.ey.restaurante.service.business;

import com.ey.restaurante.controller.dto.request.CreateReservacionRequest;
import com.ey.restaurante.controller.dto.response.ReservacionResponse;
import com.ey.restaurante.exception.HorarioInvalidoApiException;
import com.ey.restaurante.exception.LimiteReservacionesApiException;
import com.ey.restaurante.exception.ReservacionNotFoundApiException;
import com.ey.restaurante.model.entity.EstadoReservacion;
import com.ey.restaurante.model.entity.Reservacion;
import com.ey.restaurante.service.ReservacionDatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservacionBusinessServiceTest {

    @Mock
    private ReservacionDatabaseService databaseService;

    @InjectMocks
    private ReservacionBusinessService businessService;

    private CreateReservacionRequest validRequest;
    private Reservacion reservacion;

    @BeforeEach
    void setUp() {
        validRequest = new CreateReservacionRequest(
                "Juan Pérez",
                "+51999888777",
                4,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 30)
        );

        reservacion = Reservacion.builder()
                .id(1L)
                .nombreCliente("Juan Pérez")
                .telefono("+51999888777")
                .numeroPersonas(4)
                .fechaReservacion(LocalDate.now().plusDays(1))
                .horaReservacion(LocalTime.of(10, 30))
                .estado(EstadoReservacion.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Crear Reservación")
    class CrearReservacion {

        @Test
        @DisplayName("Debe crear reservación exitosamente a las 10:00 AM")
        void debeCrearReservacionExitosamente() {
            when(databaseService.countReservacionesActivasPorFecha(any())).thenReturn(0L);
            when(databaseService.save(any())).thenReturn(reservacion);

            ReservacionResponse response = businessService.crearReservacion(validRequest);

            assertThat(response).isNotNull();
            assertThat(response.nombreCliente()).isEqualTo("Juan Pérez");
            assertThat(response.estado()).isEqualTo(EstadoReservacion.ACTIVA);
            verify(databaseService).save(any(Reservacion.class));
        }

        @Test
        @DisplayName("Debe rechazar reservación a las 8:00 AM (fuera de horario)")
        void debeRechazarReservacionAntesDeLas9AM() {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(8, 0)
            );

            assertThatThrownBy(() -> businessService.crearReservacion(request))
                    .isInstanceOf(HorarioInvalidoApiException.class)
                    .hasMessageContaining("08:00")
                    .hasMessageContaining("fuera del horario permitido");
        }

        @Test
        @DisplayName("Debe rechazar reservación a las 1:00 PM (fuera de horario)")
        void debeRechazarReservacionDespuesDeLas12PM() {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(13, 0)
            );

            assertThatThrownBy(() -> businessService.crearReservacion(request))
                    .isInstanceOf(HorarioInvalidoApiException.class)
                    .hasMessageContaining("13:00")
                    .hasMessageContaining("fuera del horario permitido");
        }

        @Test
        @DisplayName("Debe aceptar reservación exactamente a las 9:00 AM")
        void debeAceptarReservacionALas9AM() {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0)
            );

            Reservacion reservacionGuardada = Reservacion.builder()
                    .id(1L)
                    .nombreCliente("Juan Pérez")
                    .telefono("+51999888777")
                    .numeroPersonas(4)
                    .fechaReservacion(LocalDate.now().plusDays(1))
                    .horaReservacion(LocalTime.of(9, 0))
                    .estado(EstadoReservacion.ACTIVA)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            when(databaseService.countReservacionesActivasPorFecha(any())).thenReturn(0L);
            when(databaseService.save(any())).thenReturn(reservacionGuardada);

            ReservacionResponse response = businessService.crearReservacion(request);

            assertThat(response).isNotNull();
            assertThat(response.horaReservacion()).isEqualTo(LocalTime.of(9, 0));
        }

        @Test
        @DisplayName("Debe aceptar reservación exactamente a las 12:00 PM")
        void debeAceptarReservacionALas12PM() {
            CreateReservacionRequest request = new CreateReservacionRequest(
                    "Juan Pérez",
                    "+51999888777",
                    4,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(12, 0)
            );

            Reservacion reservacionGuardada = Reservacion.builder()
                    .id(1L)
                    .nombreCliente("Juan Pérez")
                    .telefono("+51999888777")
                    .numeroPersonas(4)
                    .fechaReservacion(LocalDate.now().plusDays(1))
                    .horaReservacion(LocalTime.of(12, 0))
                    .estado(EstadoReservacion.ACTIVA)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            when(databaseService.countReservacionesActivasPorFecha(any())).thenReturn(0L);
            when(databaseService.save(any())).thenReturn(reservacionGuardada);

            ReservacionResponse response = businessService.crearReservacion(request);

            assertThat(response).isNotNull();
            assertThat(response.horaReservacion()).isEqualTo(LocalTime.of(12, 0));
        }

        @Test
        @DisplayName("Debe rechazar la 6ta reservación del día")
        void debeRechazarSextaReservacion() {
            when(databaseService.countReservacionesActivasPorFecha(any())).thenReturn(5L);

            assertThatThrownBy(() -> businessService.crearReservacion(validRequest))
                    .isInstanceOf(LimiteReservacionesApiException.class)
                    .hasMessageContaining("límite máximo de 5 reservaciones");
        }

        @Test
        @DisplayName("Debe permitir crear 5 reservaciones")
        void debePermitirCincoReservaciones() {
            when(databaseService.countReservacionesActivasPorFecha(any())).thenReturn(4L);
            when(databaseService.save(any())).thenReturn(reservacion);

            ReservacionResponse response = businessService.crearReservacion(validRequest);

            assertThat(response).isNotNull();
            verify(databaseService).save(any(Reservacion.class));
        }
    }

    @Nested
    @DisplayName("Listar Reservaciones")
    class ListarReservaciones {

        @Test
        @DisplayName("Debe listar todas las reservaciones")
        void debeListarReservaciones() {
            when(databaseService.findAll()).thenReturn(List.of(reservacion));

            List<ReservacionResponse> reservaciones = businessService.listarReservaciones();

            assertThat(reservaciones).hasSize(1);
            assertThat(reservaciones.get(0).nombreCliente()).isEqualTo("Juan Pérez");
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay reservaciones")
        void debeRetornarListaVacia() {
            when(databaseService.findAll()).thenReturn(List.of());

            List<ReservacionResponse> reservaciones = businessService.listarReservaciones();

            assertThat(reservaciones).isEmpty();
        }
    }

    @Nested
    @DisplayName("Obtener Reservación por ID")
    class ObtenerReservacionPorId {

        @Test
        @DisplayName("Debe obtener reservación por ID")
        void debeObtenerReservacionPorId() {
            when(databaseService.findById(1L)).thenReturn(Optional.of(reservacion));

            ReservacionResponse response = businessService.obtenerReservacionPorId(1L);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando reservación no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            when(databaseService.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.obtenerReservacionPorId(999L))
                    .isInstanceOf(ReservacionNotFoundApiException.class)
                    .hasMessageContaining("999")
                    .hasMessageContaining("no encontrada");
        }
    }

    @Nested
    @DisplayName("Cancelar Reservación")
    class CancelarReservacion {

        @Test
        @DisplayName("Debe cancelar reservación exitosamente")
        void debeCancelarReservacion() {
            Reservacion reservacionCancelada = Reservacion.builder()
                    .id(1L)
                    .nombreCliente("Juan Pérez")
                    .telefono("+51999888777")
                    .numeroPersonas(4)
                    .fechaReservacion(LocalDate.now().plusDays(1))
                    .horaReservacion(LocalTime.of(10, 30))
                    .estado(EstadoReservacion.CANCELADA)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            when(databaseService.findById(1L)).thenReturn(Optional.of(reservacion));
            when(databaseService.save(any())).thenReturn(reservacionCancelada);

            ReservacionResponse response = businessService.cancelarReservacion(1L);

            assertThat(response.estado()).isEqualTo(EstadoReservacion.CANCELADA);
            verify(databaseService).save(any(Reservacion.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción al cancelar reservación inexistente")
        void debeLanzarExcepcionAlCancelarInexistente() {
            when(databaseService.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.cancelarReservacion(999L))
                    .isInstanceOf(ReservacionNotFoundApiException.class);
        }

        @Test
        @DisplayName("Después de cancelar, debe permitir nueva reservación")
        void debeLanzarExcepcionDespuesDeCancelar() {
            when(databaseService.countReservacionesActivasPorFecha(any())).thenReturn(4L);
            when(databaseService.save(any())).thenReturn(reservacion);

            ReservacionResponse response = businessService.crearReservacion(validRequest);

            assertThat(response).isNotNull();
        }
    }
}
