package com.ey.app.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApiException(ApiException ex) {
    log.error("ApiException: {}", ex.getMessage());
    ApiError error = ex.getError();
    return new ResponseEntity<>(error, error.status());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
    log.error("Validation error: {} field errors", ex.getBindingResult().getErrorCount());
    Map<String, String> details = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    ApiError error =
        new ApiError("VALIDATION_ERROR", "Error de validacion", details, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(error, error.status());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    ApiError error =
        new ApiError(
            "INTERNAL_ERROR",
            "Error interno del servidor",
            Map.of(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(error, error.status());
  }
}
