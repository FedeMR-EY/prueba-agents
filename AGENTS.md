# AGENTS.md

## Stack Tecnológico

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.2+ |
| PostgreSQL | 16+ |
| Docker/Docker Compose | Local testing |
| Gestor de dependencias | Maven |

## Plugins Requeridos

- **FMT Format (Google)**: Formateo de código
- **Jacoco**: Cobertura de tests (mínimo 80%)

## Formato de Configuración

Usar **YAML** para todos los archivos de configuración.

---

## Estructura del Proyecto

```
java/com/ey/app/
├── config/
├── controller/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── *Resource.java
├── exception/
├── model/
│   └── entity/
├── repository/
├── service/
│   ├── business/
│   └── *DatabaseService.java
└── App.java
```

---

## Convenciones de Código

### Principios
- Seguir **SOLID** y **Clean Architecture**

### Inyección de Dependencias
```java
// CORRECTO: Constructor explícito
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// INCORRECTO: No usar
@Autowired
@RequiredArgsConstructor
```

### Lombok
| Usar | No Usar |
|------|---------|
| `@Getter`, `@Setter`, `@Builder`, `@Slf4j` | `@Data`, `@RequiredArgsConstructor` |

### Logging
Siempre usar `@Slf4j` de Lombok.

### Documentación API
Siempre usar **Swagger/OpenAPI**.

---

## Controllers

### Regla Principal
**Los controllers NO contienen lógica.** Solo delegan a un `BusinessService`.

### Definición (Interface)
Usar sufijo `Resource`:

```java
@RequestMapping("/v1/auth")
public interface AuthResource {

    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    ResponseEntity<RegisterUserResponse> registerUser(
        @RequestBody @Valid RegisterUserRequest request);

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    ResponseEntity<LoginResponse> login(
        @RequestBody @Valid LoginRequest request);
}
```

### Implementación
El controller solo delega al servicio de negocio correspondiente:

```java
@Slf4j
@RestController
public class AuthController implements AuthResource {

    private final AuthBusinessService authBusinessService;

    public AuthController(AuthBusinessService authBusinessService) {
        this.authBusinessService = authBusinessService;
    }

    @Override
    public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest registerUserRequest) {
        return authBusinessService.registerUser(registerUserRequest);
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        return authBusinessService.login(loginRequest);
    }
}
```

### Flujo de Responsabilidades
```
Controller → BusinessService → DatabaseService (impl) → Repository
   (delega)      (lógica)           (CRUD)                (JPA)
```

---

## DTOs

### Reglas de Nomenclatura
| Tipo | Sufijo | Ejemplo |
|------|--------|---------|
| Request | `Request` | `RegisterUserRequest` |
| Response | `Response` | `LoginResponse` |
| Otros | Sin sufijo | `UserProfile`, `OrderItem` |

### Implementación
- Usar **Java Records**
- Anotar con `@Builder`
- **NO** usar sufijo/prefijo "DTO"

```java
@Builder
public record RegisterUserRequest(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String name
) {}

@Builder
public record LoginResponse(
    String token,
    Long expiresIn
) {}
```

### Ubicación
Los DTOs se crean en el paquete del contexto donde se usan:
- Controller DTOs → `controller/dto/request/` o `controller/dto/response/`

---

## Services

### DatabaseService (Interface Base)
```java
public interface DatabaseService<T> {
    T save(T entity);
    List<T> getAll();
    T findById(UUID id);
    void deleteById(UUID id);
}
```

### Implementación de DatabaseService
**Los repositorios SOLO se inyectan en clases que implementan `DatabaseService`.**

```java
@Slf4j
@Service
public class UserDatabaseService implements DatabaseService<User> {

    private final UserRepository userRepository;

    public UserDatabaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
```

### Business Services
Ubicar en `service/business/`. Contienen la lógica de negocio y consumen `DatabaseService`, **nunca repositorios directamente**.

```java
@Slf4j
@Service
public class AuthBusinessService {

    private final UserDatabaseService userDatabaseService;

    public AuthBusinessService(UserDatabaseService userDatabaseService) {
        this.userDatabaseService = userDatabaseService;
    }

    public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest request) {
        // Lógica de negocio aquí
    }
}
```

---

## Testing

### Frameworks
- **JUnit 5** + **Mockito**: Tests unitarios
- **MockMvc**: Tests de integración HTTP
- **TestContainers**: Tests de integración con BD

### Reglas Críticas
1. **Cobertura mínima: 80%**
2. **Evitar levantar contexto Spring** salvo necesidad absoluta
3. Si es necesario contexto, usar slices (`@WebMvcTest`, `@DataJpaTest`)

```java
// Test unitario (preferido)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
}

// Test integración (solo cuando necesario)
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
}
```

---

## Dependencias Maven

```xml
<dependencies>
    <!-- Core -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Dev -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Checklist Rápido

- [ ] Constructor explícito para DI (no `@Autowired`)
- [ ] `@Slf4j` en todas las clases
- [ ] DTOs como Records con `@Builder`
- [ ] Controllers como interfaces `*Resource`
- [ ] Controllers solo delegan a `BusinessService` (sin lógica)
- [ ] Repositorios solo en implementaciones de `DatabaseService`
- [ ] Tests unitarios sin contexto Spring
- [ ] Cobertura >= 80%
- [ ] Swagger documentado
- [ ] Configuración en YAML
