package com.ey.app.service.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ey.app.controller.dto.request.CreateAnimalRequest;
import com.ey.app.controller.dto.request.UpdateAnimalRequest;
import com.ey.app.controller.dto.response.AnimalResponse;
import com.ey.app.exception.AnimalNotFoundApiException;
import com.ey.app.exception.VeterinariaLlenaApiException;
import com.ey.app.model.entity.Animal;
import com.ey.app.service.AnimalDatabaseService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AnimalBusinessServiceTest {

  @Mock private AnimalDatabaseService animalDatabaseService;

  private AnimalBusinessService animalBusinessService;

  @BeforeEach
  void setUp() {
    animalBusinessService = new AnimalBusinessService(animalDatabaseService);
  }

  @Test
  void createAnimal_shouldCreateAnimal_whenCapacityAvailable() {
    CreateAnimalRequest request =
        CreateAnimalRequest.builder()
            .especie("Perro")
            .edad(3)
            .nombre("Firulais")
            .peso(10.5)
            .build();

    when(animalDatabaseService.count()).thenReturn(5L);
    when(animalDatabaseService.save(any(Animal.class)))
        .thenAnswer(
            invocation -> {
              Animal animal = invocation.getArgument(0);
              animal.setId(UUID.randomUUID());
              return animal;
            });

    ResponseEntity<AnimalResponse> response = animalBusinessService.createAnimal(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Firulais", response.getBody().nombre());
    assertEquals("Perro", response.getBody().especie());
    verify(animalDatabaseService).save(any(Animal.class));
  }

  @Test
  void createAnimal_shouldThrowException_whenCapacityFull() {
    CreateAnimalRequest request =
        CreateAnimalRequest.builder()
            .especie("Perro")
            .edad(3)
            .nombre("Firulais")
            .peso(10.5)
            .build();

    when(animalDatabaseService.count()).thenReturn(10L);

    assertThrows(
        VeterinariaLlenaApiException.class, () -> animalBusinessService.createAnimal(request));
    verify(animalDatabaseService, never()).save(any(Animal.class));
  }

  @Test
  void getAllAnimals_shouldReturnAllAnimals() {
    List<Animal> animals = List.of(createAnimal(), createAnimal());
    when(animalDatabaseService.getAll()).thenReturn(animals);

    ResponseEntity<List<AnimalResponse>> response = animalBusinessService.getAllAnimals();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
  }

  @Test
  void getAnimalById_shouldReturnAnimal_whenExists() {
    UUID id = UUID.randomUUID();
    Animal animal = createAnimal();
    animal.setId(id);
    when(animalDatabaseService.findById(id)).thenReturn(animal);

    ResponseEntity<AnimalResponse> response = animalBusinessService.getAnimalById(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(id, response.getBody().id());
  }

  @Test
  void getAnimalById_shouldThrowException_whenNotExists() {
    UUID id = UUID.randomUUID();
    when(animalDatabaseService.findById(id)).thenReturn(null);

    assertThrows(AnimalNotFoundApiException.class, () -> animalBusinessService.getAnimalById(id));
  }

  @Test
  void updateAnimal_shouldUpdateAnimal_whenExists() {
    UUID id = UUID.randomUUID();
    Animal existingAnimal = createAnimal();
    existingAnimal.setId(id);

    UpdateAnimalRequest request =
        UpdateAnimalRequest.builder().especie("Gato").edad(5).nombre("Michi").peso(4.0).build();

    when(animalDatabaseService.findById(id)).thenReturn(existingAnimal);
    when(animalDatabaseService.save(any(Animal.class))).thenReturn(existingAnimal);

    ResponseEntity<AnimalResponse> response = animalBusinessService.updateAnimal(id, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(animalDatabaseService).save(existingAnimal);
  }

  @Test
  void updateAnimal_shouldThrowException_whenNotExists() {
    UUID id = UUID.randomUUID();
    UpdateAnimalRequest request =
        UpdateAnimalRequest.builder().especie("Gato").edad(5).nombre("Michi").peso(4.0).build();

    when(animalDatabaseService.findById(id)).thenReturn(null);

    assertThrows(
        AnimalNotFoundApiException.class, () -> animalBusinessService.updateAnimal(id, request));
  }

  @Test
  void deleteAnimal_shouldDeleteAnimal_whenExists() {
    UUID id = UUID.randomUUID();
    Animal existingAnimal = createAnimal();
    existingAnimal.setId(id);

    when(animalDatabaseService.findById(id)).thenReturn(existingAnimal);
    doNothing().when(animalDatabaseService).deleteById(id);

    ResponseEntity<Void> response = animalBusinessService.deleteAnimal(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(animalDatabaseService).deleteById(id);
  }

  @Test
  void deleteAnimal_shouldThrowException_whenNotExists() {
    UUID id = UUID.randomUUID();
    when(animalDatabaseService.findById(id)).thenReturn(null);

    assertThrows(AnimalNotFoundApiException.class, () -> animalBusinessService.deleteAnimal(id));
  }

  private Animal createAnimal() {
    Animal animal = new Animal();
    animal.setId(UUID.randomUUID());
    animal.setEspecie("Perro");
    animal.setEdad(3);
    animal.setNombre("Firulais");
    animal.setPeso(10.5);
    return animal;
  }
}
