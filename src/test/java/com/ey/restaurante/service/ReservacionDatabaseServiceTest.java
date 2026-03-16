package com.ey.restaurante.service;

import com.ey.restaurante.model.entity.EstadoReservacion;
import com.ey.restaurante.model.entity.Reservacion;
import com.ey.restaurante.repository.ReservacionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservacionDatabaseServiceTest {

    @Mock
    private ReservacionRepository reservacionRepository;

    @InjectMocks
    private ReservacionDatabaseService databaseService;

    private Reservacion reservacion;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Save")
    class Save {

        @Test
        @DisplayName("Debe guardar reservación")
        void debeGuardarReservacion() {
            when(reservacionRepository.save(any())).thenReturn(reservacion);

            Reservacion saved = databaseService.save(reservacion);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(1L);
            verify(reservacionRepository).save(reservacion);
        }
    }

    @Nested
    @DisplayName("FindById")
    class FindById {

        @Test
        @DisplayName("Debe encontrar reservación por ID")
        void debeEncontrarPorId() {
            when(reservacionRepository.findById(1L)).thenReturn(Optional.of(reservacion));

            Optional<Reservacion> found = databaseService.findById(1L);

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe retornar empty cuando no existe")
        void debeRetornarEmptyNoExiste() {
            when(reservacionRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Reservacion> found = databaseService.findById(999L);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("FindAll")
    class FindAll {

        @Test
        @DisplayName("Debe retornar todas las reservaciones")
        void debeRetornarTodas() {
            when(reservacionRepository.findAll()).thenReturn(List.of(reservacion));

            List<Reservacion> reservaciones = databaseService.findAll();

            assertThat(reservaciones).hasSize(1);
        }

        @Test
        @DisplayName("Debe retornar lista vacía")
        void debeRetornarListaVacia() {
            when(reservacionRepository.findAll()).thenReturn(List.of());

            List<Reservacion> reservaciones = databaseService.findAll();

            assertThat(reservaciones).isEmpty();
        }
    }

    @Nested
    @DisplayName("DeleteById")
    class DeleteById {

        @Test
        @DisplayName("Debe eliminar reservación por ID")
        void debeEliminarPorId() {
            doNothing().when(reservacionRepository).deleteById(1L);

            databaseService.deleteById(1L);

            verify(reservacionRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("ExistsById")
    class ExistsById {

        @Test
        @DisplayName("Debe retornar true si existe")
        void debeRetornarTrueSiExiste() {
            when(reservacionRepository.existsById(1L)).thenReturn(true);

            boolean exists = databaseService.existsById(1L);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false si no existe")
        void debeRetornarFalseSiNoExiste() {
            when(reservacionRepository.existsById(999L)).thenReturn(false);

            boolean exists = databaseService.existsById(999L);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("CountReservacionesActivasPorFecha")
    class CountReservacionesActivasPorFecha {

        @Test
        @DisplayName("Debe contar reservaciones activas por fecha")
        void debeContarReservacionesActivas() {
            LocalDate fecha = LocalDate.now().plusDays(1);
            when(reservacionRepository.countReservacionesActivasPorFecha(fecha)).thenReturn(3L);

            long count = databaseService.countReservacionesActivasPorFecha(fecha);

            assertThat(count).isEqualTo(3L);
        }

        @Test
        @DisplayName("Debe retornar 0 si no hay reservaciones")
        void debeRetornarCeroSinReservaciones() {
            LocalDate fecha = LocalDate.now().plusDays(1);
            when(reservacionRepository.countReservacionesActivasPorFecha(fecha)).thenReturn(0L);

            long count = databaseService.countReservacionesActivasPorFecha(fecha);

            assertThat(count).isZero();
        }
    }
}
