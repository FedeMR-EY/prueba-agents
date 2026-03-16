# Skill: create-controller

Genera un Controller completo siguiendo la arquitectura establecida.

## Uso

```
/create-controller <NombreDominio> [ruta-base]
```

**Ejemplos:**
- `/create-controller User` → Genera `UserResource` y `UserController`
- `/create-controller Product /v1/products` → Con ruta personalizada

## Instrucciones

Cuando el usuario invoque esta skill:

1. **Solicitar información** si no se proporcionó:
   - Nombre del dominio (ej: `User`, `Product`, `Order`)
   - Ruta base del endpoint (ej: `/v1/users`)
   - Endpoints a crear (ej: `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`)

2. **Generar la Interface Resource** en `controller/<NombreDominio>Resource.java`:

```java
package {basePackage}.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import {basePackage}.controller.dto.request.*;
import {basePackage}.controller.dto.response.*;
import java.util.List;
import java.util.UUID;

@RequestMapping("{rutaBase}")
public interface {NombreDominio}Resource {

    @GetMapping(produces = "application/json")
    ResponseEntity<List<{NombreDominio}Response>> getAll();

    @GetMapping(value = "/{id}", produces = "application/json")
    ResponseEntity<{NombreDominio}Response> getById(@PathVariable UUID id);

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<{NombreDominio}Response> create(
        @RequestBody @Valid Create{NombreDominio}Request request);

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    ResponseEntity<{NombreDominio}Response> update(
        @PathVariable UUID id,
        @RequestBody @Valid Update{NombreDominio}Request request);

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id);
}
```

3. **Generar el Controller** en `controller/<NombreDominio>Controller.java`:

```java
package {basePackage}.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import {basePackage}.controller.dto.request.*;
import {basePackage}.controller.dto.response.*;
import {basePackage}.service.business.{NombreDominio}BusinessService;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class {NombreDominio}Controller implements {NombreDominio}Resource {

    private final {NombreDominio}BusinessService {nombreDominio}BusinessService;

    public {NombreDominio}Controller({NombreDominio}BusinessService {nombreDominio}BusinessService) {
        this.{nombreDominio}BusinessService = {nombreDominio}BusinessService;
    }

    @Override
    public ResponseEntity<List<{NombreDominio}Response>> getAll() {
        log.debug("GET all {nombreDominio}s");
        return {nombreDominio}BusinessService.getAll();
    }

    @Override
    public ResponseEntity<{NombreDominio}Response> getById(UUID id) {
        log.debug("GET {nombreDominio} by id: {}", id);
        return {nombreDominio}BusinessService.getById(id);
    }

    @Override
    public ResponseEntity<{NombreDominio}Response> create(Create{NombreDominio}Request request) {
        log.debug("POST create {nombreDominio}");
        return {nombreDominio}BusinessService.create(request);
    }

    @Override
    public ResponseEntity<{NombreDominio}Response> update(UUID id, Update{NombreDominio}Request request) {
        log.debug("PUT update {nombreDominio} id: {}", id);
        return {nombreDominio}BusinessService.update(id, request);
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        log.debug("DELETE {nombreDominio} id: {}", id);
        return {nombreDominio}BusinessService.delete(id);
    }
}
```

4. **Reglas importantes:**
   - El controller **NUNCA** contiene lógica de negocio
   - Solo delega al `BusinessService` correspondiente
   - Usar inyección por constructor (no `@Autowired`)
   - Siempre incluir `@Slf4j` para logging
   - Los métodos del Resource deben tener anotaciones de Swagger/OpenAPI

5. **Después de crear los archivos:**
   - Preguntar si desea crear los DTOs asociados (`/create-dto`)
   - Preguntar si desea crear el BusinessService (`/create-service`)
