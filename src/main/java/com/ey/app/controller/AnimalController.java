package com.ey.app.controller;

import com.ey.app.controller.dto.request.CreateAnimalRequest;
import com.ey.app.controller.dto.request.UpdateAnimalRequest;
import com.ey.app.controller.dto.response.AnimalResponse;
import com.ey.app.service.business.AnimalBusinessService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AnimalController implements AnimalResource {

  private final AnimalBusinessService animalBusinessService;

  public AnimalController(AnimalBusinessService animalBusinessService) {
    this.animalBusinessService = animalBusinessService;
  }

  @Override
  public ResponseEntity<AnimalResponse> createAnimal(CreateAnimalRequest request) {
    return animalBusinessService.createAnimal(request);
  }

  @Override
  public ResponseEntity<List<AnimalResponse>> getAllAnimals() {
    return animalBusinessService.getAllAnimals();
  }

  @Override
  public ResponseEntity<AnimalResponse> getAnimalById(UUID id) {
    return animalBusinessService.getAnimalById(id);
  }

  @Override
  public ResponseEntity<AnimalResponse> updateAnimal(UUID id, UpdateAnimalRequest request) {
    return animalBusinessService.updateAnimal(id, request);
  }

  @Override
  public ResponseEntity<Void> deleteAnimal(UUID id) {
    return animalBusinessService.deleteAnimal(id);
  }
}
