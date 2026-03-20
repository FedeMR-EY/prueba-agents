package com.ey.app.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class AnimeNotFoundException extends ApiException {

    public AnimeNotFoundException(Long malId) {
        super(
                "Anime not found",
                new ApiError(
                        "ANIME_NOT_FOUND",
                        "Anime not found in Jikan API",
                        Map.of("malId", malId.toString()),
                        HttpStatus.NOT_FOUND));
    }

    public AnimeNotFoundException(String id) {
        super(
                "Anime not found",
                new ApiError(
                        "ANIME_NOT_FOUND",
                        "Anime not found in favorites",
                        Map.of("id", id),
                        HttpStatus.NOT_FOUND));
    }
}
