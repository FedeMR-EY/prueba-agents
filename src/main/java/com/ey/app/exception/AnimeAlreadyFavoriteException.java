package com.ey.app.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class AnimeAlreadyFavoriteException extends ApiException {

    public AnimeAlreadyFavoriteException(Long malId) {
        super(
                "Anime already in favorites",
                new ApiError(
                        "ANIME_ALREADY_FAVORITE",
                        "Anime already in favorites",
                        Map.of("malId", malId.toString()),
                        HttpStatus.CONFLICT));
    }
}
