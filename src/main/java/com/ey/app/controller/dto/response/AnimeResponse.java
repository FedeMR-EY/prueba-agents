package com.ey.app.controller.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AnimeResponse(
        Long malId,
        String title,
        String titleEnglish,
        String titleJapanese,
        String imageUrl,
        String synopsis,
        Double score,
        Integer episodes,
        String status,
        String type,
        String rating,
        Integer rank,
        Integer popularity,
        String season,
        Integer year,
        List<String> genres,
        List<String> studios) {}
