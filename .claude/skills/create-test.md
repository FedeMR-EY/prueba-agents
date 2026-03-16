# Skill: create-test

Genera tests unitarios y de integración siguiendo las mejores prácticas.

## Uso

```
/create-test <Clase> [tipo]
```

**Ejemplos:**
- `/create-test UserService` → Test unitario para UserService
- `/create-test UserController integration` → Test de integración
- `/create-test UserBusinessService unit` → Test unitario explícito

## Instrucciones

Cuando el usuario invoque esta skill:

1. **Identificar el tipo de clase** y generar el test apropiado:

| Clase | Tipo de Test | Anotaciones |
|-------|--------------|-------------|
| `*Service` | Unitario | `@ExtendWith(MockitoExtension.class)` |
| `*Controller` | Integración | `@WebMvcTest` |
| `*Repository` | Integración | `@DataJpaTest` |
| `*BusinessService` | Unitario | `@ExtendWith(MockitoExtension.class)` |

## 2. Plantillas de Tests

### Test Unitario para Service

```java
package {basePackage}.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class {Clase}Test {

    @Mock
    private {Dependencia}Repository {dependencia}Repository;

    @InjectMocks
    private {Clase} {claseInstance};

    private {Entidad} testEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEntity = {Entidad}.builder()
            .id(testId)
            // .campo(valor)
            .build();
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return entity when exists")
        void should_return_entity_when_exists() {
            // Arrange
            when({dependencia}Repository.findById(testId))
                .thenReturn(Optional.of(testEntity));

            // Act
            {Entidad} result = {claseInstance}.findById(testId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testId);
            verify({dependencia}Repository).findById(testId);
        }

        @Test
        @DisplayName("should return null when not exists")
        void should_return_null_when_not_exists() {
            // Arrange
            when({dependencia}Repository.findById(testId))
                .thenReturn(Optional.empty());

            // Act
            {Entidad} result = {claseInstance}.findById(testId);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("should save and return entity")
        void should_save_and_return_entity() {
            // Arrange
            when({dependencia}Repository.save(any({Entidad}.class)))
                .thenReturn(testEntity);

            // Act
            {Entidad} result = {claseInstance}.save(testEntity);

            // Assert
            assertThat(result).isEqualTo(testEntity);
            verify({dependencia}Repository).save(testEntity);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("should delete entity by id")
        void should_delete_entity_by_id() {
            // Arrange
            doNothing().when({dependencia}Repository).deleteById(testId);

            // Act
            {claseInstance}.deleteById(testId);

            // Assert
            verify({dependencia}Repository).deleteById(testId);
        }
    }
}
```

### Test Unitario para BusinessService

```java
package {basePackage}.service.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class {Clase}Test {

    @Mock
    private {Entidad}DatabaseService {entidad}DatabaseService;

    @InjectMocks
    private {Clase} {claseInstance};

    private {Entidad} testEntity;
    private UUID testId;
    private Create{Entidad}Request createRequest;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEntity = {Entidad}.builder()
            .id(testId)
            .build();
        createRequest = Create{Entidad}Request.builder()
            .build();
    }

    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("should return list of responses")
        void should_return_list_of_responses() {
            // Arrange
            when({entidad}DatabaseService.getAll())
                .thenReturn(List.of(testEntity));

            // Act
            ResponseEntity<List<{Entidad}Response>> response = {claseInstance}.getAll();

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when no entities")
        void should_return_empty_list_when_no_entities() {
            // Arrange
            when({entidad}DatabaseService.getAll()).thenReturn(List.of());

            // Act
            ResponseEntity<List<{Entidad}Response>> response = {claseInstance}.getAll();

            // Assert
            assertThat(response.getBody()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("should return response when exists")
        void should_return_response_when_exists() {
            // Arrange
            when({entidad}DatabaseService.findById(testId))
                .thenReturn(testEntity);

            // Act
            ResponseEntity<{Entidad}Response> response = {claseInstance}.getById(testId);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("should throw NotFoundApiException when not exists")
        void should_throw_exception_when_not_exists() {
            // Arrange
            when({entidad}DatabaseService.findById(testId))
                .thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> {claseInstance}.getById(testId))
                .isInstanceOf(NotFoundApiException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create and return response with 201 status")
        void should_create_and_return_response() {
            // Arrange
            when({entidad}DatabaseService.save(any()))
                .thenReturn(testEntity);

            // Act
            ResponseEntity<{Entidad}Response> response = {claseInstance}.create(createRequest);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
        }
    }
}
```

### Test de Integración para Controller

```java
package {basePackage}.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({Clase}.class)
class {Clase}Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private {Entidad}BusinessService {entidad}BusinessService;

    private {Entidad}Response testResponse;
    private Create{Entidad}Request createRequest;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testResponse = {Entidad}Response.builder()
            .id(testId)
            .build();
        createRequest = Create{Entidad}Request.builder()
            .build();
    }

    @Nested
    @DisplayName("GET /v1/{entidades}")
    class GetAll {

        @Test
        @DisplayName("should return 200 with list")
        void should_return_200_with_list() throws Exception {
            // Arrange
            when({entidad}BusinessService.getAll())
                .thenReturn(ResponseEntity.ok(List.of(testResponse)));

            // Act & Assert
            mockMvc.perform(get("/v1/{entidades}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    @DisplayName("GET /v1/{entidades}/{id}")
    class GetById {

        @Test
        @DisplayName("should return 200 when exists")
        void should_return_200_when_exists() throws Exception {
            // Arrange
            when({entidad}BusinessService.getById(testId))
                .thenReturn(ResponseEntity.ok(testResponse));

            // Act & Assert
            mockMvc.perform(get("/v1/{entidades}/" + testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("POST /v1/{entidades}")
    class Create {

        @Test
        @DisplayName("should return 201 when valid request")
        void should_return_201_when_valid() throws Exception {
            // Arrange
            when({entidad}BusinessService.create(any()))
                .thenReturn(ResponseEntity.status(201).body(testResponse));

            // Act & Assert
            mockMvc.perform(post("/v1/{entidades}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 when invalid request")
        void should_return_400_when_invalid() throws Exception {
            // Arrange - request inválido (campos requeridos vacíos)
            String invalidRequest = "{}";

            // Act & Assert
            mockMvc.perform(post("/v1/{entidades}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                .andExpect(status().isBadRequest());
        }
    }
}
```

## 3. Reglas de Nomenclatura

```java
// Patrón: should_<resultado>_when_<condición>
void should_return_user_when_valid_id()
void should_throw_exception_when_not_found()
void should_return_empty_list_when_no_data()
void should_create_user_when_valid_request()
```

## 4. Post-Creación: Validación Obligatoria

**IMPORTANTE:** Después de crear cualquier test, SIEMPRE ejecutar `/run-tests` para:
1. Verificar que el test compila
2. Verificar que el test pasa
3. Verificar que el test tiene sentido (no es un falso positivo)
4. Verificar la cobertura

```bash
# Ejecutar el test creado
mvn test -Dtest={Clase}Test

# Verificar cobertura
mvn test jacoco:report
```

## 5. Casos Mínimos a Cubrir

Para cada método testeado, incluir al menos:
- [ ] Happy path (caso exitoso)
- [ ] Caso de error esperado (ej: not found)
- [ ] Validación de inputs (null, vacío)
- [ ] Edge cases relevantes
