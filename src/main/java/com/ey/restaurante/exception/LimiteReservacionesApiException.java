package com.ey.restaurante.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LimiteReservacionesApiException extends ApiException {

    private static final String ERROR_CODE = "LIMITE_RESERVACIONES_EXCEDIDO";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LimiteReservacionesApiException(LocalDate fecha) {
        super("Se ha alcanzado el límite máximo de 5 reservaciones para el día " + fecha.format(FORMATTER),
              HttpStatus.CONFLICT, ERROR_CODE);
    }
}
