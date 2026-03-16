package com.ey.restaurante.exception;

import org.springframework.http.HttpStatus;

public class ReservacionNotFoundApiException extends ApiException {

    private static final String ERROR_CODE = "RESERVACION_NOT_FOUND";

    public ReservacionNotFoundApiException(Long id) {
        super("Reservación con ID " + id + " no encontrada", HttpStatus.NOT_FOUND, ERROR_CODE);
    }
}
