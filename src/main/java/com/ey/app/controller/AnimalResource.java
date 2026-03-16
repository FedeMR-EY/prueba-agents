package com.ey.app.controller;

import com.ey.app.controller.dto.request.CreateAnimalRequest;
import com.ey.app.controller.dto.request.UpdateAnimalRequest;
import com.ey.app.controller.dto.response.AnimalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Animales", description = "API de registro de animales")
@RequestMapping("/v1/animales")
public interface AnimalResource {

  @Operation(summary = "Registrar un nuevo animal")
  @PostMapping(produces = "application/json", consumes = "application/json")
  ResponseEntity<AnimalResponse> createAnimal(@RequestBody @Valid CreateAnimalRequest request);

  @Operation(summary = "Obtener todos los animales")
  @GetMapping(produces = "application/json")
  ResponseEntity<List<AnimalResponse>> getAllAnimals();

  @Operation(summary = "Obtener un animal por ID")
  @GetMapping(value = "/{id}", produces = "application/json")
  ResponseEntity<AnimalResponse> getAnimalById(@PathVariable UUID id);

  @Operation(summary = "Actualizar un animal")
  @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
  ResponseEntity<AnimalResponse> updateAnimal(
      @PathVariable UUID id, @RequestBody @Valid UpdateAnimalRequest request);

  @Operation(summary = "Eliminar un animal")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteAnimal(@PathVariable UUID id);
}
