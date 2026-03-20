package com.ey.app.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SaveFavoriteRequest(@NotNull Long malId) {}
