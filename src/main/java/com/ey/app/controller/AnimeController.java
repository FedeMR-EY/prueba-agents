package com.ey.app.controller;

import com.ey.app.controller.dto.request.SaveFavoriteRequest;
import com.ey.app.controller.dto.response.AnimeResponse;
import com.ey.app.controller.dto.response.AnimeSearchResponse;
import com.ey.app.controller.dto.response.FavoriteAnimeResponse;
import com.ey.app.service.business.AnimeBusinessService;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AnimeController implements AnimeResource {

    private final AnimeBusinessService animeBusinessService;

    public AnimeController(AnimeBusinessService animeBusinessService) {
        this.animeBusinessService = animeBusinessService;
    }

    @Override
    public ResponseEntity<AnimeSearchResponse> searchAnime(
            String query, Integer page, Integer limit) {
        return animeBusinessService.searchAnime(query, page, limit);
    }

    @Override
    public ResponseEntity<AnimeResponse> getAnimeById(Long malId) {
        return animeBusinessService.getAnimeById(malId);
    }

    @Override
    public ResponseEntity<AnimeSearchResponse> getTopAnime(Integer page, Integer limit) {
        return animeBusinessService.getTopAnime(page, limit);
    }

    @Override
    public ResponseEntity<FavoriteAnimeResponse> saveFavorite(SaveFavoriteRequest request) {
        return animeBusinessService.saveFavorite(request);
    }

    @Override
    public ResponseEntity<List<FavoriteAnimeResponse>> getAllFavorites() {
        return animeBusinessService.getAllFavorites();
    }

    @Override
    public ResponseEntity<FavoriteAnimeResponse> getFavoriteById(UUID id) {
        return animeBusinessService.getFavoriteById(id);
    }

    @Override
    public ResponseEntity<Void> deleteFavorite(UUID id) {
        return animeBusinessService.deleteFavorite(id);
    }
}
