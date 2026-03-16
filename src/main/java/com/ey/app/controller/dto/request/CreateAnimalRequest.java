package com.ey.app.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CreateAnimalRequest(
    @NotBlank(message = "La especie es obligatoria") String especie,
    @NotNull(message = "La edad es obligatoria") @Positive(message = "La edad debe ser positiva")
        Integer edad,
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotNull(message = "El peso es obligatorio") @Positive(message = "El peso debe ser positivo")
        Double peso) {}
