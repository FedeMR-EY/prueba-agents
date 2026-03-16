package com.ey.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ey.app.model.entity.Animal;
import com.ey.app.repository.AnimalRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnimalDatabaseServiceTest {

  @Mock private AnimalRepository animalRepository;

  private AnimalDatabaseService animalDatabaseService;

  @BeforeEach
  void setUp() {
    animalDatabaseService = new AnimalDatabaseService(animalRepository);
  }

  @Test
  void save_shouldSaveAnimal() {
    Animal animal = createAnimal();
    when(animalRepository.save(animal)).thenReturn(animal);

    Animal result = animalDatabaseService.save(animal);

    assertNotNull(result);
    assertEquals("Firulais", result.getNombre());
    verify(animalRepository).save(animal);
  }

  @Test
  void getAll_shouldReturnAllAnimals() {
    List<Animal> animals = List.of(createAnimal(), createAnimal());
    when(animalRepository.findAll()).thenReturn(animals);

    List<Animal> result = animalDatabaseService.getAll();

    assertEquals(2, result.size());
    verify(animalRepository).findAll();
  }

  @Test
  void findById_shouldReturnAnimal_whenExists() {
    UUID id = UUID.randomUUID();
    Animal animal = createAnimal();
    animal.setId(id);
    when(animalRepository.findById(id)).thenReturn(Optional.of(animal));

    Animal result = animalDatabaseService.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(animalRepository).findById(id);
  }

  @Test
  void findById_shouldReturnNull_whenNotExists() {
    UUID id = UUID.randomUUID();
    when(animalRepository.findById(id)).thenReturn(Optional.empty());

    Animal result = animalDatabaseService.findById(id);

    assertNull(result);
    verify(animalRepository).findById(id);
  }

  @Test
  void deleteById_shouldDeleteAnimal() {
    UUID id = UUID.randomUUID();
    doNothing().when(animalRepository).deleteById(id);

    animalDatabaseService.deleteById(id);

    verify(animalRepository).deleteById(id);
  }

  @Test
  void count_shouldReturnCount() {
    when(animalRepository.count()).thenReturn(5L);

    long result = animalDatabaseService.count();

    assertEquals(5L, result);
    verify(animalRepository).count();
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
