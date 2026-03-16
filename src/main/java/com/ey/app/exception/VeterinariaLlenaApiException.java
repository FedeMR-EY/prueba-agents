package com.ey.app.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class VeterinariaLlenaApiException extends ApiException {

  public VeterinariaLlenaApiException() {
    super(
        "La veterinaria esta llena",
        new ApiError(
            "VETERINARIA_LLENA",
            "La veterinaria esta llena",
            Map.of("capacidadMaxima", "10"),
            HttpStatus.BAD_REQUEST));
  }
}
