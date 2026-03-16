package com.ey.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ey.app.controller.dto.request.CreateAnimalRequest;
import com.ey.app.controller.dto.request.UpdateAnimalRequest;
import com.ey.app.controller.dto.response.AnimalResponse;
import com.ey.app.exception.AnimalNotFoundApiException;
import com.ey.app.exception.GlobalExceptionHandler;
import com.ey.app.exception.VeterinariaLlenaApiException;
import com.ey.app.service.business.AnimalBusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnimalController.class)
@Import(GlobalExceptionHandler.class)
class AnimalControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AnimalBusinessService animalBusinessService;

  @Test
  void createAnimal_shouldReturn201_whenValidRequest() throws Exception {
    CreateAnimalRequest request =
        CreateAnimalRequest.builder()
            .especie("Perro")
            .edad(3)
            .nombre("Firulais")
            .peso(10.5)
            .build();

    UUID id = UUID.randomUUID();
    AnimalResponse response =
        AnimalResponse.builder()
            .id(id)
            .especie("Perro")
            .edad(3)
            .nombre("Firulais")
            .peso(10.5)
            .build();

    when(animalBusinessService.createAnimal(any(CreateAnimalRequest.class)))
        .thenReturn(ResponseEntity.status(201).body(response));

    mockMvc
        .perform(
            post("/v1/animales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nombre").value("Firulais"))
        .andExpect(jsonPath("$.especie").value("Perro"));
  }

  @Test
  void createAnimal_shouldReturn400_whenVeterinariaLlena() throws Exception {
    CreateAnimalRequest request =
        CreateAnimalRequest.builder()
            .especie("Perro")
            .edad(3)
            .nombre("Firulais")
            .peso(10.5)
            .build();

    when(animalBusinessService.createAnimal(any(CreateAnimalRequest.class)))
        .thenThrow(new VeterinariaLlenaApiException());

    mockMvc
        .perform(
            post("/v1/animales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("VETERINARIA_LLENA"))
        .andExpect(jsonPath("$.title").value("La veterinaria esta llena"));
  }

  @Test
  void createAnimal_shouldReturn400_whenInvalidRequest() throws Exception {
    CreateAnimalRequest request =
        CreateAnimalRequest.builder().especie("").edad(null).nombre("").peso(null).build();

    mockMvc
        .perform(
            post("/v1/animales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"));
  }

  @Test
  void getAllAnimals_shouldReturn200() throws Exception {
    List<AnimalResponse> animals =
        List.of(
            AnimalResponse.builder()
                .id(UUID.randomUUID())
                .especie("Perro")
                .edad(3)
                .nombre("Firulais")
                .peso(10.5)
                .build());

    when(animalBusinessService.getAllAnimals()).thenReturn(ResponseEntity.ok(animals));

    mockMvc
        .perform(get("/v1/animales").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nombre").value("Firulais"));
  }

  @Test
  void getAnimalById_shouldReturn200_whenExists() throws Exception {
    UUID id = UUID.randomUUID();
    AnimalResponse response =
        AnimalResponse.builder()
            .id(id)
            .especie("Perro")
            .edad(3)
            .nombre("Firulais")
            .peso(10.5)
            .build();

    when(animalBusinessService.getAnimalById(id)).thenReturn(ResponseEntity.ok(response));

    mockMvc
        .perform(get("/v1/animales/" + id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Firulais"));
  }

  @Test
  void getAnimalById_shouldReturn404_whenNotExists() throws Exception {
    UUID id = UUID.randomUUID();

    when(animalBusinessService.getAnimalById(id)).thenThrow(new AnimalNotFoundApiException(id));

    mockMvc
        .perform(get("/v1/animales/" + id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.type").value("NOT_FOUND"));
  }

  @Test
  void updateAnimal_shouldReturn200_whenExists() throws Exception {
    UUID id = UUID.randomUUID();
    UpdateAnimalRequest request =
        UpdateAnimalRequest.builder().especie("Gato").edad(5).nombre("Michi").peso(4.0).build();

    AnimalResponse response =
        AnimalResponse.builder().id(id).especie("Gato").edad(5).nombre("Michi").peso(4.0).build();

    when(animalBusinessService.updateAnimal(eq(id), any(UpdateAnimalRequest.class)))
        .thenReturn(ResponseEntity.ok(response));

    mockMvc
        .perform(
            put("/v1/animales/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Michi"));
  }

  @Test
  void deleteAnimal_shouldReturn204_whenExists() throws Exception {
    UUID id = UUID.randomUUID();

    when(animalBusinessService.deleteAnimal(id)).thenReturn(ResponseEntity.noContent().build());

    mockMvc.perform(delete("/v1/animales/" + id)).andExpect(status().isNoContent());
  }
}
