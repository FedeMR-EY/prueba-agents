# Skill: check-standards

Verifica que el código cumple con los estándares y convenciones del proyecto.

## Uso

```
/check-standards [ruta]
```

**Ejemplos:**
- `/check-standards` → Verifica todo el proyecto
- `/check-standards src/main/java/com/app/service/` → Verifica solo servicios

## Instrucciones

Cuando el usuario invoque esta skill, realizar las siguientes verificaciones:

## 1. Verificaciones de Arquitectura

### Controllers
- [ ] Existe interface `*Resource` con `@RequestMapping`
- [ ] Controller implementa su Resource correspondiente
- [ ] Controller **NO** contiene lógica de negocio (solo delega)
- [ ] Controller usa `@Slf4j`
- [ ] Inyección por constructor (no `@Autowired`)

### Services
- [ ] `*DatabaseService` implementa `DatabaseService<T>`
- [ ] Repositorios **SOLO** inyectados en DatabaseService
- [ ] `*BusinessService` usa DatabaseService, no Repository
- [ ] Todos los servicios tienen `@Slf4j`
- [ ] Inyección por constructor

### DTOs
- [ ] Son Java Records
- [ ] Tienen `@Builder`
- [ ] NO usan sufijo/prefijo "DTO"
- [ ] Requests tienen validaciones (`@NotBlank`, etc.)
- [ ] Ubicados en `controller/dto/request/` o `controller/dto/response/`

### Excepciones
- [ ] Todas extienden `ApiException`
- [ ] Nomenclatura `*ApiException`
- [ ] Existe `GlobalExceptionHandler` con `@ControllerAdvice`

## 2. Verificaciones de Código

### Lombok
- [ ] Usa: `@Getter`, `@Setter`, `@Builder`, `@Slf4j`
- [ ] **NO** usa: `@Data`, `@RequiredArgsConstructor`, `@AllArgsConstructor`

### Inyección de Dependencias
```java
// CORRECTO
private final UserRepository userRepository;

public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
}

// INCORRECTO - Reportar
@Autowired
private UserRepository userRepository;

@RequiredArgsConstructor  // No usar
```

### Logging
- [ ] Todas las clases tienen `@Slf4j`
- [ ] Usa `log.debug()`, `log.info()`, `log.error()` apropiadamente
- [ ] **NO** usa `System.out.println()`

## 3. Verificaciones de Testing

- [ ] Cobertura >= 80% (verificar con Jacoco)
- [ ] Tests unitarios usan `@ExtendWith(MockitoExtension.class)`
- [ ] Tests de integración usan slices (`@WebMvcTest`, `@DataJpaTest`)
- [ ] **NO** levantan contexto Spring completo innecesariamente

## 4. Verificaciones de Configuración

- [ ] Archivos de configuración en YAML (no .properties)
- [ ] Swagger/OpenAPI configurado
- [ ] `application.yml` con perfiles (dev, prod, test)

## 5. Output del Check

Generar un reporte con el siguiente formato:

```
## Standards Check Report

### Summary
- Total checks: X
- Passed: Y
- Failed: Z
- Warnings: W

### Issues Found

#### Critical
- [FAIL] Controllers: AuthController contiene lógica de negocio (línea 45)
- [FAIL] DI: UserService usa @Autowired en lugar de constructor

#### Warnings
- [WARN] Missing @Slf4j in ProductMapper
- [WARN] Test coverage is 75% (minimum 80%)

#### Passed
- [OK] All DTOs are Records with @Builder
- [OK] All exceptions extend ApiException
- [OK] No @Data annotations found

### Recommendations
1. Refactorizar AuthController para mover lógica a AuthBusinessService
2. Cambiar @Autowired por inyección de constructor en UserService
3. Agregar tests para alcanzar 80% de cobertura
```

## 6. Auto-fix (Opcional)

Si el usuario lo solicita, ofrecer corregir automáticamente:
- Agregar `@Slf4j` faltantes
- Convertir `@Autowired` a constructor injection
- Mover DTOs a ubicación correcta

Preguntar antes de realizar cambios automáticos.
