package com.ey.app.service.business;

import com.ey.app.controller.dto.request.CreateAnimalRequest;
import com.ey.app.controller.dto.request.UpdateAnimalRequest;
import com.ey.app.controller.dto.response.AnimalResponse;
import com.ey.app.exception.AnimalNotFoundApiException;
import com.ey.app.exception.VeterinariaLlenaApiException;
import com.ey.app.model.entity.Animal;
import com.ey.app.service.AnimalDatabaseService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnimalBusinessService {

  private static final int CAPACIDAD_MAXIMA = 10;

  private final AnimalDatabaseService animalDatabaseService;

  public AnimalBusinessService(AnimalDatabaseService animalDatabaseService) {
    this.animalDatabaseService = animalDatabaseService;
  }

  public ResponseEntity<AnimalResponse> createAnimal(CreateAnimalRequest request) {
    log.info("Creando nuevo animal: {}", request.nombre());

    if (animalDatabaseService.count() >= CAPACIDAD_MAXIMA) {
      throw new VeterinariaLlenaApiException();
    }

    Animal animal = new Animal();
    animal.setEspecie(request.especie());
    animal.setEdad(request.edad());
    animal.setNombre(request.nombre());
    animal.setPeso(request.peso());

    Animal saved = animalDatabaseService.save(animal);
    return new ResponseEntity<>(toResponse(saved), HttpStatus.CREATED);
  }

  public ResponseEntity<List<AnimalResponse>> getAllAnimals() {
    log.info("Obteniendo lista de animales");
    List<AnimalResponse> animals =
        animalDatabaseService.getAll().stream().map(this::toResponse).toList();
    return ResponseEntity.ok(animals);
  }

  public ResponseEntity<AnimalResponse> getAnimalById(UUID id) {
    log.info("Obteniendo animal con id: {}", id);
    Animal animal = animalDatabaseService.findById(id);
    if (animal == null) {
      throw new AnimalNotFoundApiException(id);
    }
    return ResponseEntity.ok(toResponse(animal));
  }

  public ResponseEntity<AnimalResponse> updateAnimal(UUID id, UpdateAnimalRequest request) {
    log.info("Actualizando animal con id: {}", id);
    Animal animal = animalDatabaseService.findById(id);
    if (animal == null) {
      throw new AnimalNotFoundApiException(id);
    }

    animal.setEspecie(request.especie());
    animal.setEdad(request.edad());
    animal.setNombre(request.nombre());
    animal.setPeso(request.peso());

    Animal updated = animalDatabaseService.save(animal);
    return ResponseEntity.ok(toResponse(updated));
  }

  public ResponseEntity<Void> deleteAnimal(UUID id) {
    log.info("Eliminando animal con id: {}", id);
    Animal animal = animalDatabaseService.findById(id);
    if (animal == null) {
      throw new AnimalNotFoundApiException(id);
    }

    animalDatabaseService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private AnimalResponse toResponse(Animal animal) {
    return AnimalResponse.builder()
        .id(animal.getId())
        .especie(animal.getEspecie())
        .edad(animal.getEdad())
        .nombre(animal.getNombre())
        .peso(animal.getPeso())
        .build();
  }
}
