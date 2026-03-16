package com.ey.restaurante.controller;

import com.ey.restaurante.controller.dto.request.CreateReservacionRequest;
import com.ey.restaurante.controller.dto.response.ReservacionResponse;
import com.ey.restaurante.service.business.ReservacionBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservacionController implements ReservacionResource {

    private final ReservacionBusinessService businessService;

    @Override
    public ResponseEntity<ReservacionResponse> crearReservacion(CreateReservacionRequest request) {
        ReservacionResponse response = businessService.crearReservacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<List<ReservacionResponse>> listarReservaciones() {
        List<ReservacionResponse> reservaciones = businessService.listarReservaciones();
        return ResponseEntity.ok(reservaciones);
    }

    @Override
    public ResponseEntity<ReservacionResponse> obtenerReservacionPorId(Long id) {
        ReservacionResponse response = businessService.obtenerReservacionPorId(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReservacionResponse> cancelarReservacion(Long id) {
        ReservacionResponse response = businessService.cancelarReservacion(id);
        return ResponseEntity.ok(response);
    }
}
