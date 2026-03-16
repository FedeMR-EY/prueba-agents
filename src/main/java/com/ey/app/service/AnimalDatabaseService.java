package com.ey.app.service;

import com.ey.app.model.entity.Animal;
import com.ey.app.repository.AnimalRepository;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnimalDatabaseService implements DatabaseService<Animal> {

  private final AnimalRepository animalRepository;

  public AnimalDatabaseService(AnimalRepository animalRepository) {
    this.animalRepository = animalRepository;
  }

  @Override
  public Animal save(Animal entity) {
    log.info("Guardando animal: {}", entity.getNombre());
    return animalRepository.save(entity);
  }

  @Override
  public List<Animal> getAll() {
    log.info("Obteniendo todos los animales");
    return animalRepository.findAll();
  }

  @Override
  public Animal findById(UUID id) {
    log.info("Buscando animal con id: {}", id);
    return animalRepository.findById(id).orElse(null);
  }

  @Override
  public void deleteById(UUID id) {
    log.info("Eliminando animal con id: {}", id);
    animalRepository.deleteById(id);
  }

  public long count() {
    return animalRepository.count();
  }
}
