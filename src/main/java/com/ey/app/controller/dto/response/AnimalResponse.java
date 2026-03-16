package com.ey.app.controller.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AnimalResponse(UUID id, String especie, Integer edad, String nombre, Double peso) {}
