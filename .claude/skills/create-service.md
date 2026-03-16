# Skill: create-service

Genera servicios (DatabaseService y/o BusinessService) siguiendo la arquitectura establecida.

## Uso

```
/create-service <NombreDominio> [tipo]
```

**Ejemplos:**
- `/create-service User` → Genera DatabaseService y BusinessService
- `/create-service Product database` → Solo DatabaseService
- `/create-service Order business` → Solo BusinessService

## Instrucciones

Cuando el usuario invoque esta skill:

1. **Solicitar información** si no se proporcionó:
   - Nombre del dominio/entidad
   - Tipo de servicio: `database`, `business`, o `all`
   - Para BusinessService: operaciones requeridas

2. **Verificar que existe la interface base** `DatabaseService<T>`:

```java
package {basePackage}.service;

import java.util.List;
import java.util.UUID;

public interface DatabaseService<T> {
    T save(T entity);
    List<T> getAll();
    T findById(UUID id);
    void deleteById(UUID id);
}
```

Si no existe, crearla primero.

3. **Generar DatabaseService** en `service/{NombreDominio}DatabaseService.java`:

```java
package {basePackage}.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import {basePackage}.model.entity.{NombreDominio};
import {basePackage}.repository.{NombreDominio}Repository;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class {NombreDominio}DatabaseService implements DatabaseService<{NombreDominio}> {

    private final {NombreDominio}Repository {nombreDominio}Repository;

    public {NombreDominio}DatabaseService({NombreDominio}Repository {nombreDominio}Repository) {
        this.{nombreDominio}Repository = {nombreDominio}Repository;
    }

    @Override
    public {NombreDominio} save({NombreDominio} entity) {
        log.debug("Saving {nombreDominio}: {}", entity);
        return {nombreDominio}Repository.save(entity);
    }

    @Override
    public List<{NombreDominio}> getAll() {
        log.debug("Finding all {nombreDominio}s");
        return {nombreDominio}Repository.findAll();
    }

    @Override
    public {NombreDominio} findById(UUID id) {
        log.debug("Finding {nombreDominio} by id: {}", id);
        return {nombreDominio}Repository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting {nombreDominio} by id: {}", id);
        {nombreDominio}Repository.deleteById(id);
    }

    // Métodos adicionales específicos del dominio
    // Ejemplo:
    // public Optional<{NombreDominio}> findByEmail(String email) {
    //     return {nombreDominio}Repository.findByEmail(email);
    // }
}
```

4. **Generar BusinessService** en `service/business/{NombreDominio}BusinessService.java`:

```java
package {basePackage}.service.business;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import {basePackage}.controller.dto.request.*;
import {basePackage}.controller.dto.response.*;
import {basePackage}.model.entity.{NombreDominio};
import {basePackage}.service.{NombreDominio}DatabaseService;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class {NombreDominio}BusinessService {

    private final {NombreDominio}DatabaseService {nombreDominio}DatabaseService;

    public {NombreDominio}BusinessService({NombreDominio}DatabaseService {nombreDominio}DatabaseService) {
        this.{nombreDominio}DatabaseService = {nombreDominio}DatabaseService;
    }

    public ResponseEntity<List<{NombreDominio}Response>> getAll() {
        List<{NombreDominio}> entities = {nombreDominio}DatabaseService.getAll();
        List<{NombreDominio}Response> responses = entities.stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    public ResponseEntity<{NombreDominio}Response> getById(UUID id) {
        {NombreDominio} entity = {nombreDominio}DatabaseService.findById(id);
        if (entity == null) {
            // Lanzar excepción apropiada según el manejo de errores del proyecto
            throw new NotFoundApiException("{NombreDominio} not found", "{NombreDominio}", id.toString());
        }
        return ResponseEntity.ok(toResponse(entity));
    }

    public ResponseEntity<{NombreDominio}Response> create(Create{NombreDominio}Request request) {
        {NombreDominio} entity = toEntity(request);
        {NombreDominio} saved = {nombreDominio}DatabaseService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    public ResponseEntity<{NombreDominio}Response> update(UUID id, Update{NombreDominio}Request request) {
        {NombreDominio} existing = {nombreDominio}DatabaseService.findById(id);
        if (existing == null) {
            throw new NotFoundApiException("{NombreDominio} not found", "{NombreDominio}", id.toString());
        }
        updateEntity(existing, request);
        {NombreDominio} updated = {nombreDominio}DatabaseService.save(existing);
        return ResponseEntity.ok(toResponse(updated));
    }

    public ResponseEntity<Void> delete(UUID id) {
        {nombreDominio}DatabaseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Mappers privados
    private {NombreDominio}Response toResponse({NombreDominio} entity) {
        return {NombreDominio}Response.builder()
            // .id(entity.getId())
            // .campo(entity.getCampo())
            .build();
    }

    private {NombreDominio} toEntity(Create{NombreDominio}Request request) {
        return {NombreDominio}.builder()
            // .campo(request.campo())
            .build();
    }

    private void updateEntity({NombreDominio} entity, Update{NombreDominio}Request request) {
        // if (request.campo() != null) {
        //     entity.setCampo(request.campo());
        // }
    }
}
```

5. **Reglas importantes:**
   - Los repositorios **SOLO** se inyectan en `DatabaseService`
   - Los `BusinessService` **SOLO** consumen `DatabaseService`, nunca repositorios
   - Usar inyección por constructor
   - Siempre incluir `@Slf4j`
   - Los mappers (toEntity, toResponse) son privados en el BusinessService

6. **Flujo de responsabilidades:**
```
Controller → BusinessService → DatabaseService → Repository
  (delega)      (lógica)           (CRUD)          (JPA)
```

7. **Después de crear:**
   - Verificar que existe el Repository correspondiente
   - Verificar que existe la Entity correspondiente
   - Crear tests unitarios con `/create-test`
