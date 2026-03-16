package com.ey.app.exception;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class AnimalNotFoundApiException extends ApiException {

  public AnimalNotFoundApiException(UUID id) {
    super(
        "Animal no encontrado",
        new ApiError(
            "NOT_FOUND",
            "Animal no encontrado",
            Map.of("animalId", id.toString()),
            HttpStatus.NOT_FOUND));
  }
}
