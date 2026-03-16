# Skill: run-tests

Ejecuta y valida tests del proyecto, asegurando que funcionan correctamente y tienen sentido.

## Uso

```
/run-tests [tipo] [ruta]
```

**Ejemplos:**
- `/run-tests` → Ejecuta todos los tests
- `/run-tests unit` → Solo tests unitarios
- `/run-tests integration` → Solo tests de integración
- `/run-tests unit src/test/java/com/app/service/` → Tests específicos

## Instrucciones

Cuando el usuario invoque esta skill o **cuando se creen/modifiquen tests**:

## 1. Ejecutar Tests

### Maven
```bash
# Todos los tests
mvn test

# Tests unitarios (sin tag de integración)
mvn test -Dgroups="unit"

# Tests de integración
mvn test -Dgroups="integration"

# Clase específica
mvn test -Dtest=UserServiceTest

# Con cobertura
mvn test jacoco:report
```

### Gradle
```bash
# Todos los tests
./gradlew test

# Con cobertura
./gradlew test jacocoTestReport
```

## 2. Validar que los Tests Tienen Sentido

Después de ejecutar, **SIEMPRE** verificar:

### 2.1 Estructura del Test
- [ ] Sigue patrón AAA (Arrange, Act, Assert)
- [ ] Nombre descriptivo: `should_<acción>_when_<condición>`
- [ ] Un assert lógico por test (o asserts relacionados)

### 2.2 Tests Unitarios
- [ ] Usan `@ExtendWith(MockitoExtension.class)`
- [ ] Mockean dependencias correctamente
- [ ] **NO** levantan contexto Spring
- [ ] Verifican comportamiento, no implementación

```java
// CORRECTO
@Test
void should_return_user_when_valid_id() {
    // Arrange
    UUID id = UUID.randomUUID();
    User expected = User.builder().id(id).name("John").build();
    when(userRepository.findById(id)).thenReturn(Optional.of(expected));

    // Act
    User result = userService.findById(id);

    // Assert
    assertThat(result).isEqualTo(expected);
    verify(userRepository).findById(id);
}

// INCORRECTO - Test sin sentido
@Test
void test1() {
    assertThat(true).isTrue();  // No prueba nada real
}
```

### 2.3 Tests de Integración
- [ ] Usan slices apropiados (`@WebMvcTest`, `@DataJpaTest`)
- [ ] Usan `@SpringBootTest` solo cuando es necesario
- [ ] Usan TestContainers para BD si es necesario
- [ ] Verifican integración real entre componentes

### 2.4 Cobertura de Casos
- [ ] Happy path cubierto
- [ ] Casos de error cubiertos
- [ ] Edge cases cubiertos (null, vacío, límites)
- [ ] Excepciones verificadas

```java
// Verificar que se cubren múltiples escenarios
@Test void should_create_user_when_valid_request() { }
@Test void should_throw_exception_when_email_exists() { }
@Test void should_throw_exception_when_request_is_null() { }
@Test void should_return_empty_list_when_no_users() { }
```

## 3. Validar Resultados

### Si los tests PASAN:
1. Verificar que realmente prueban algo significativo
2. Revisar cobertura de código (mínimo 80%)
3. Confirmar que no hay tests que siempre pasan (falsos positivos)

### Si los tests FALLAN:
1. Analizar el mensaje de error
2. Determinar si es:
   - Error en el código de producción → Corregir código
   - Error en el test → Corregir test
   - Test desactualizado → Actualizar test
3. Ejecutar de nuevo tras corrección

## 4. Reporte de Validación

Generar reporte con:

```
## Test Execution Report

### Execution Summary
- Total tests: 45
- Passed: 43
- Failed: 2
- Skipped: 0
- Time: 12.5s

### Coverage
- Line coverage: 82%
- Branch coverage: 75%
- Minimum required: 80%

### Failed Tests
1. UserServiceTest.should_throw_when_user_not_found
   - Expected: NotFoundApiException
   - Actual: NullPointerException
   - Issue: Missing null check in findById

2. AuthControllerTest.should_return_401_when_invalid_token
   - Expected: 401 Unauthorized
   - Actual: 500 Internal Server Error
   - Issue: Exception handler not catching TokenException

### Test Quality Issues
- [WARN] UserServiceTest.testUser - Nombre no descriptivo
- [WARN] ProductServiceTest - Falta test para caso de error
- [OK] OrderServiceTest - Buena cobertura de casos

### Recommendations
1. Agregar null check en UserService.findById()
2. Agregar handler para TokenException en GlobalExceptionHandler
3. Renombrar tests con nombres descriptivos
4. Agregar tests de error para ProductService
```

## 5. Ejecución Automática

**IMPORTANTE:** Esta skill debe ejecutarse automáticamente cuando:
- Se crean nuevos tests
- Se modifican tests existentes
- Se completa una feature que incluye tests
- El usuario solicita validación de tests

## 6. Comandos Útiles

```bash
# Ver cobertura en HTML
open target/site/jacoco/index.html

# Ejecutar test específico con output detallado
mvn test -Dtest=UserServiceTest -X

# Ejecutar tests en paralelo
mvn test -T 4

# Saltar tests (NO recomendado)
mvn install -DskipTests
```
