package com.ey.restaurante.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HorarioInvalidoApiException extends ApiException {

    private static final String ERROR_CODE = "HORARIO_INVALIDO";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public HorarioInvalidoApiException(LocalTime hora) {
        super("La hora " + hora.format(FORMATTER) + " está fuera del horario permitido (09:00 - 12:00)",
              HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
}
