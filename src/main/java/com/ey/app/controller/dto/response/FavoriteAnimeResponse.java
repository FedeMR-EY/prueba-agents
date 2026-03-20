package com.ey.app.controller.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FavoriteAnimeResponse(
        UUID id,
        Long malId,
        String title,
        String titleEnglish,
        String imageUrl,
        String synopsis,
        Double score,
        Integer episodes,
        String status,
        LocalDateTime createdAt) {}
