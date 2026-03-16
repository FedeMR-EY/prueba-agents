# AGENTS.md

Guía de estándares para proyectos de APIs REST.

## Stack Tecnológico

| Tecnología | Versión | Notas |
|------------|---------|-------|
| Java | 17+ / 21 | LTS recomendado |
| Spring Boot | 3.x | |
| Base de Datos | PostgreSQL / MySQL / H2 | Según ambiente |
| Contenedores | Docker / Docker Compose | Para ambientes locales |
| Build Tool | Maven / Gradle | |

## Plugins Recomendados

- **Formateo**: Google Java Format / Spotless
- **Cobertura**: Jacoco (mínimo 80%)
- **Análisis**: SpotBugs, Checkstyle (opcional)

## Formato de Configuración

Usar **YAML** para archivos de configuración (`application.yml`).

---

## Estructura del Proyecto

```
src/main/java/{basePackage}/
├── config/              # Configuraciones (OpenAPI, Security, etc.)
├── controller/
│   ├── dto/
│   │   ├── request/     # DTOs de entrada
│   │   └── response/    # DTOs de salida
│   ├── *Resource.java   # Interfaces con endpoints
│   └── *Controller.java # Implementaciones
├── exception/           # ApiException, ApiError, GlobalExceptionHandler
├── model/
│   └── entity/          # Entidades JPA
├── repository/          # Repositorios JPA
├── service/
│   ├── business/        # Lógica de negocio (*BusinessService)
│   └── *DatabaseService.java  # Acceso a datos
└── Application.java
```

---

## Convenciones de Código

### Principios
- **SOLID** y **Clean Architecture**
- Separación clara de responsabilidades

### Inyección de Dependencias
```java
// CORRECTO: Constructor explícito
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// INCORRECTO: No usar
@Autowired  // Evitar
@RequiredArgsConstructor  // Evitar
```

### Lombok

| Permitido | No Usar |
|-----------|---------|
| `@Getter`, `@Setter`, `@Builder`, `@Slf4j` | `@Data`, `@RequiredArgsConstructor`, `@AllArgsConstructor` |

### Logging
- Siempre usar `@Slf4j` de Lombok
- Nunca `System.out.println()`

### Documentación API
- Swagger/OpenAPI obligatorio

---

## Flujo de Responsabilidades

```
Controller → BusinessService → DatabaseService → Repository
  (delega)      (lógica)           (CRUD)          (JPA)
```

| Capa | Responsabilidad | Reglas |
|------|-----------------|--------|
| Controller | Recibir HTTP, delegar | Sin lógica de negocio |
| BusinessService | Lógica de negocio | Usa DatabaseService, nunca Repository |
| DatabaseService | Operaciones CRUD | Único lugar donde se inyecta Repository |
| Repository | Acceso a BD | Interface JPA |

---

## Reglas Rápidas

### Controllers
- Interface `*Resource` define endpoints
- Controller implementa Resource y delega a BusinessService
- **Sin lógica de negocio**

### DTOs
- Usar **Java Records** con `@Builder`
- Sufijos: `*Request`, `*Response`
- **Sin** prefijo/sufijo "DTO"
- Validaciones Jakarta en requests

### Services
- `DatabaseService<T>` → CRUD genérico
- Repositorios **solo** en DatabaseService
- BusinessService consume DatabaseService

### Excepciones
- Todas extienden `ApiException`
- `GlobalExceptionHandler` con `@ControllerAdvice`
- Respuestas con `ApiError`

---

## Testing

### Reglas Críticas
1. **Cobertura mínima: 80%**
2. **Tests deben ejecutarse y pasar** antes de considerar completada una tarea
3. **Tests deben validar comportamiento real**, no ser falsos positivos
4. Evitar levantar contexto Spring completo

### Tipos de Tests

| Tipo | Anotación | Uso |
|------|-----------|-----|
| Unitario | `@ExtendWith(MockitoExtension.class)` | Services, lógica |
| Controller | `@WebMvcTest` | Endpoints HTTP |
| Repository | `@DataJpaTest` | Queries JPA |
| Integración | `@SpringBootTest` + TestContainers | E2E con BD real |

### Validación Obligatoria de Tests

Cuando se creen o modifiquen tests, **SIEMPRE**:
1. Ejecutar los tests para verificar que compilan y pasan
2. Verificar que prueban comportamiento real (no falsos positivos)
3. Verificar cobertura de casos: happy path, errores, edge cases
4. Usar `/run-tests` para validación completa

---

## Skills Disponibles

Usa estos comandos para generar código siguiendo los estándares:

| Comando | Descripción |
|---------|-------------|
| `/create-controller` | Genera Resource + Controller |
| `/create-dto` | Genera Request/Response DTOs |
| `/create-service` | Genera DatabaseService + BusinessService |
| `/create-exception` | Genera excepciones + ApiError |
| `/create-test` | Genera tests unitarios/integración |
| `/run-tests` | Ejecuta y valida tests |
| `/check-standards` | Verifica cumplimiento de estándares |

---

## Checklist Rápido

- [ ] Constructor explícito para DI
- [ ] `@Slf4j` en todas las clases
- [ ] DTOs como Records con `@Builder`
- [ ] Controllers solo delegan (sin lógica)
- [ ] Repositorios solo en DatabaseService
- [ ] Tests ejecutados y validados
- [ ] Cobertura >= 80%
- [ ] Swagger documentado
