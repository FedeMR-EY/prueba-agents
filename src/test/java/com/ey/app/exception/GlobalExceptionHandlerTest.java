package com.ey.app.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleApiException_ReturnsApiError() {
        ApiError apiError =
                new ApiError(
                        "ANIME_NOT_FOUND",
                        "Anime not found",
                        Map.of("malId", "1"),
                        HttpStatus.NOT_FOUND);

        ApiException apiException = new ApiException("Anime not found", apiError);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleApiException(apiException);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ANIME_NOT_FOUND", response.getBody().type());
        assertEquals("Anime not found", response.getBody().title());
    }

    @Test
    void handleApiException_WithCause_ReturnsApiError() {
        ApiError apiError =
                new ApiError(
                        "EXTERNAL_API_ERROR",
                        "External API error",
                        Map.of("api", "Jikan"),
                        HttpStatus.SERVICE_UNAVAILABLE);

        RuntimeException cause = new RuntimeException("Connection refused");
        ApiException apiException = new ApiException("External API error", cause, apiError);

        ResponseEntity<ApiError> response = globalExceptionHandler.handleApiException(apiException);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("EXTERNAL_API_ERROR", response.getBody().type());
    }

    @Test
    void handleValidationException_ReturnsValidationError() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "saveFavoriteRequest");
        bindingResult.addError(
                new FieldError("saveFavoriteRequest", "malId", "must not be null"));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response =
                globalExceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().type());
        assertEquals("Validation failed", response.getBody().title());
        assertTrue(response.getBody().detail().containsKey("malId"));
    }

    @Test
    void handleGenericException_ReturnsInternalError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ApiError> response =
                globalExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().type());
        assertEquals("An unexpected error occurred", response.getBody().title());
    }

    @Test
    void handleGenericException_WithNullMessage_ReturnsInternalError() {
        Exception exception = new RuntimeException((String) null);

        ResponseEntity<ApiError> response =
                globalExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unknown error", response.getBody().detail().get("message"));
    }
}
