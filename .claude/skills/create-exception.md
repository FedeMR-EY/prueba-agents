# Skill: create-exception

Genera excepciones personalizadas siguiendo el patrón de manejo de errores del proyecto.

## Uso

```
/create-exception <NombreExcepcion> [HttpStatus]
```

**Ejemplos:**
- `/create-exception NotFound 404` → NotFoundApiException
- `/create-exception Unauthorized 401`
- `/create-exception BusinessValidation 422`

## Instrucciones

Cuando el usuario invoque esta skill:

1. **Verificar que existen las clases base**. Si no existen, crearlas primero:

**ApiError** en `exception/ApiError.java`:
```java
package {basePackage}.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public record ApiError(
    String type,
    String title,
    Map<String, String> detail,
    HttpStatus status
) {}
```

**ApiException** en `exception/ApiException.java`:
```java
package {basePackage}.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiError error;

    public ApiException(String message, Throwable cause, ApiError error) {
        super(message, cause);
        this.error = error;
    }

    public ApiException(String message, ApiError error) {
        super(message);
        this.error = error;
    }
}
```

**GlobalExceptionHandler** en `exception/GlobalExceptionHandler.java`:
```java
package {basePackage}.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        log.error("API Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
            .status(ex.getError().status())
            .body(ex.getError());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiError apiError = new ApiError(
            "VALIDATION_ERROR",
            "Validation failed",
            errors,
            HttpStatus.BAD_REQUEST
        );

        log.error("Validation Exception: {}", errors);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ApiError apiError = new ApiError(
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Unknown error"),
            HttpStatus.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity.internalServerError().body(apiError);
    }
}
```

2. **Generar la excepción específica** en `exception/{Nombre}ApiException.java`:

```java
package {basePackage}.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class {Nombre}ApiException extends ApiException {

    public {Nombre}ApiException(String message) {
        super(message, new ApiError(
            "{TIPO_ERROR}",
            "{Título del error}",
            Map.of(),
            HttpStatus.{HTTP_STATUS}
        ));
    }

    public {Nombre}ApiException(String message, String resourceType, String resourceId) {
        super(message, new ApiError(
            "{TIPO_ERROR}",
            resourceType + " {acción}",
            Map.of("resourceType", resourceType, "resourceId", resourceId),
            HttpStatus.{HTTP_STATUS}
        ));
    }

    public {Nombre}ApiException(String message, Map<String, String> details) {
        super(message, new ApiError(
            "{TIPO_ERROR}",
            "{Título del error}",
            details,
            HttpStatus.{HTTP_STATUS}
        ));
    }
}
```

3. **Excepciones comunes predefinidas:**

| Nombre | HTTP Status | Type | Uso |
|--------|-------------|------|-----|
| NotFoundApiException | 404 | NOT_FOUND | Recurso no encontrado |
| UnauthorizedApiException | 401 | UNAUTHORIZED | No autenticado |
| ForbiddenApiException | 403 | FORBIDDEN | Sin permisos |
| BadRequestApiException | 400 | BAD_REQUEST | Request inválido |
| ConflictApiException | 409 | CONFLICT | Conflicto (duplicados) |
| ValidationApiException | 422 | VALIDATION_ERROR | Error de validación de negocio |

4. **Ejemplo de uso en código:**

```java
// En un BusinessService
public ResponseEntity<UserResponse> getById(UUID id) {
    User user = userDatabaseService.findById(id);
    if (user == null) {
        throw new NotFoundApiException("User not found", "User", id.toString());
    }
    return ResponseEntity.ok(toResponse(user));
}

// Con detalles personalizados
throw new ValidationApiException("Invalid operation", Map.of(
    "field", "email",
    "reason", "Email already registered"
));
```

5. **Reglas importantes:**
   - Todas las excepciones del proyecto **DEBEN** extender `ApiException`
   - Nomenclatura: `{Nombre}ApiException`
   - Siempre incluir un constructor con `Map<String, String> details`
   - El `GlobalExceptionHandler` transforma cualquier excepción no controlada en `ApiException`

6. **Después de crear:**
   - Agregar el handler específico en `GlobalExceptionHandler` si es necesario
   - Crear tests para verificar el manejo correcto
