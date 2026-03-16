# Skill: create-dto

Genera DTOs (Request/Response) siguiendo los estándares del proyecto.

## Uso

```
/create-dto <NombreDominio> [tipo]
```

**Ejemplos:**
- `/create-dto User` → Genera todos los DTOs para User
- `/create-dto Product request` → Solo genera requests
- `/create-dto Order response` → Solo genera responses

## Instrucciones

Cuando el usuario invoque esta skill:

1. **Solicitar información** si no se proporcionó:
   - Nombre del dominio (ej: `User`, `Product`)
   - Campos del DTO con sus tipos y validaciones
   - Tipo de DTO: `request`, `response`, o `all`

2. **Generar Request DTOs** en `controller/dto/request/`:

```java
package {basePackage}.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record Create{NombreDominio}Request(
    @NotBlank String campo1,
    @NotNull Integer campo2,
    @Email String email
) {}
```

```java
package {basePackage}.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record Update{NombreDominio}Request(
    String campo1,
    Integer campo2
) {}
```

3. **Generar Response DTOs** en `controller/dto/response/`:

```java
package {basePackage}.controller.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record {NombreDominio}Response(
    UUID id,
    String campo1,
    Integer campo2,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

4. **Reglas de nomenclatura:**

| Tipo | Prefijo/Sufijo | Ejemplo |
|------|----------------|---------|
| Crear | `Create*Request` | `CreateUserRequest` |
| Actualizar | `Update*Request` | `UpdateUserRequest` |
| Respuesta | `*Response` | `UserResponse` |
| Búsqueda | `Search*Request` | `SearchUserRequest` |
| Filtros | `*Filter` | `UserFilter` |

5. **Reglas importantes:**
   - Siempre usar **Java Records**
   - Siempre anotar con `@Builder`
   - **NUNCA** usar sufijo/prefijo "DTO"
   - Usar validaciones de `jakarta.validation` en requests
   - Los responses no llevan validaciones
   - Incluir campos de auditoría en responses (`createdAt`, `updatedAt`) si aplica

6. **Validaciones comunes:**

```java
@NotBlank    // String no nulo ni vacío
@NotNull     // Campo requerido
@NotEmpty    // Colección no vacía
@Email       // Formato email
@Size(min=, max=)  // Tamaño
@Min @Max    // Valores numéricos
@Pattern(regexp=)  // Regex
@Past @Future      // Fechas
```

7. **Después de crear:**
   - Verificar que los imports estén correctos
   - Confirmar que la ubicación es la correcta según el contexto
