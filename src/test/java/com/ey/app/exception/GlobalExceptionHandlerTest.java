package com.ey.app.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleApiException_shouldReturnCorrectResponse() {
    ApiException exception = new VeterinariaLlenaApiException();

    ResponseEntity<ApiError> response = globalExceptionHandler.handleApiException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("VETERINARIA_LLENA", response.getBody().type());
    assertEquals("La veterinaria esta llena", response.getBody().title());
  }

  @Test
  void handleApiException_shouldHandleAnimalNotFound() {
    UUID id = UUID.randomUUID();
    ApiException exception = new AnimalNotFoundApiException(id);

    ResponseEntity<ApiError> response = globalExceptionHandler.handleApiException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("NOT_FOUND", response.getBody().type());
  }

  @Test
  void handleValidationException_shouldReturnValidationError() {
    Object target = new Object();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
    bindingResult.addError(new FieldError("target", "nombre", "El nombre es obligatorio"));

    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<ApiError> response = globalExceptionHandler.handleValidationException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("VALIDATION_ERROR", response.getBody().type());
    assertTrue(response.getBody().detail().containsKey("nombre"));
  }

  @Test
  void handleGenericException_shouldReturnInternalError() {
    Exception exception = new RuntimeException("Unexpected error");

    ResponseEntity<ApiError> response = globalExceptionHandler.handleGenericException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INTERNAL_ERROR", response.getBody().type());
  }

  @Test
  void apiException_withCause_shouldPreserveCause() {
    Throwable cause = new RuntimeException("Original error");
    ApiError error =
        new ApiError("TEST_ERROR", "Test title", Map.of(), HttpStatus.INTERNAL_SERVER_ERROR);
    ApiException exception = new ApiException("Test message", cause, error);

    assertEquals("Test message", exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals(error, exception.getError());
  }
}
