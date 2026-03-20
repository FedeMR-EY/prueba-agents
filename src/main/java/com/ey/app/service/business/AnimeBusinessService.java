package com.ey.app.service.business;

import com.ey.app.clients.jikan.JikanClient;
import com.ey.app.clients.jikan.dto.response.JikanAnimeByIdResponse;
import com.ey.app.clients.jikan.dto.response.JikanAnimeResponse;
import com.ey.app.controller.dto.request.SaveFavoriteRequest;
import com.ey.app.controller.dto.response.AnimeResponse;
import com.ey.app.controller.dto.response.AnimeSearchResponse;
import com.ey.app.controller.dto.response.FavoriteAnimeResponse;
import com.ey.app.exception.AnimeAlreadyFavoriteException;
import com.ey.app.exception.AnimeNotFoundException;
import com.ey.app.exception.ExternalApiException;
import com.ey.app.model.entity.FavoriteAnime;
import com.ey.app.service.FavoriteAnimeDatabaseService;
import feign.FeignException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnimeBusinessService {

    private final JikanClient jikanClient;
    private final FavoriteAnimeDatabaseService favoriteAnimeDatabaseService;

    public AnimeBusinessService(
            JikanClient jikanClient, FavoriteAnimeDatabaseService favoriteAnimeDatabaseService) {
        this.jikanClient = jikanClient;
        this.favoriteAnimeDatabaseService = favoriteAnimeDatabaseService;
    }

    public ResponseEntity<AnimeSearchResponse> searchAnime(String query, Integer page, Integer limit) {
        log.info("Searching anime with query: {}, page: {}, limit: {}", query, page, limit);

        try {
            JikanAnimeResponse response = jikanClient.searchAnime(query, page, limit);
            AnimeSearchResponse searchResponse = mapToAnimeSearchResponse(response);
            return ResponseEntity.ok(searchResponse);

        } catch (FeignException e) {
            log.error("Error calling Jikan API: {}", e.getMessage(), e);
            throw new ExternalApiException("Jikan", e.getMessage());
        }
    }

    public ResponseEntity<AnimeResponse> getAnimeById(Long malId) {
        log.info("Getting anime by malId: {}", malId);

        try {
            JikanAnimeByIdResponse response = jikanClient.getAnimeById(malId);

            if (response.data() == null) {
                throw new AnimeNotFoundException(malId);
            }

            AnimeResponse animeResponse = mapToAnimeResponse(response.data());
            return ResponseEntity.ok(animeResponse);

        } catch (FeignException.NotFound e) {
            log.error("Anime not found with malId: {}", malId);
            throw new AnimeNotFoundException(malId);
        } catch (FeignException e) {
            log.error("Error calling Jikan API: {}", e.getMessage(), e);
            throw new ExternalApiException("Jikan", e.getMessage());
        }
    }

    public ResponseEntity<AnimeSearchResponse> getTopAnime(Integer page, Integer limit) {
        log.info("Getting top anime with page: {}, limit: {}", page, limit);

        try {
            JikanAnimeResponse response = jikanClient.getTopAnime(page, limit);
            AnimeSearchResponse searchResponse = mapToAnimeSearchResponse(response);
            return ResponseEntity.ok(searchResponse);

        } catch (FeignException e) {
            log.error("Error calling Jikan API: {}", e.getMessage(), e);
            throw new ExternalApiException("Jikan", e.getMessage());
        }
    }

    public ResponseEntity<FavoriteAnimeResponse> saveFavorite(SaveFavoriteRequest request) {
        log.info("Saving favorite anime with malId: {}", request.malId());

        if (favoriteAnimeDatabaseService.existsByMalId(request.malId())) {
            log.warn("Anime already in favorites with malId: {}", request.malId());
            throw new AnimeAlreadyFavoriteException(request.malId());
        }

        try {
            JikanAnimeByIdResponse jikanResponse = jikanClient.getAnimeById(request.malId());

            if (jikanResponse.data() == null) {
                throw new AnimeNotFoundException(request.malId());
            }

            JikanAnimeResponse.AnimeData animeData = jikanResponse.data();

            FavoriteAnime favoriteAnime =
                    FavoriteAnime.builder()
                            .malId(animeData.malId())
                            .title(animeData.title())
                            .titleEnglish(animeData.titleEnglish())
                            .imageUrl(
                                    animeData.images() != null && animeData.images().jpg() != null
                                            ? animeData.images().jpg().imageUrl()
                                            : null)
                            .synopsis(animeData.synopsis())
                            .score(animeData.score())
                            .episodes(animeData.episodes())
                            .status(animeData.status())
                            .createdAt(LocalDateTime.now())
                            .build();

            FavoriteAnime saved = favoriteAnimeDatabaseService.save(favoriteAnime);
            FavoriteAnimeResponse response = mapToFavoriteAnimeResponse(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (FeignException.NotFound e) {
            log.error("Anime not found in Jikan API with malId: {}", request.malId());
            throw new AnimeNotFoundException(request.malId());
        } catch (FeignException e) {
            log.error("Error calling Jikan API: {}", e.getMessage(), e);
            throw new ExternalApiException("Jikan", e.getMessage());
        }
    }

    public ResponseEntity<List<FavoriteAnimeResponse>> getAllFavorites() {
        log.info("Getting all favorite anime");

        List<FavoriteAnime> favorites = favoriteAnimeDatabaseService.getAll();
        List<FavoriteAnimeResponse> response =
                favorites.stream().map(this::mapToFavoriteAnimeResponse).toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<FavoriteAnimeResponse> getFavoriteById(UUID id) {
        log.info("Getting favorite anime by id: {}", id);

        try {
            FavoriteAnime favorite = favoriteAnimeDatabaseService.findById(id);
            FavoriteAnimeResponse response = mapToFavoriteAnimeResponse(favorite);
            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            log.error("Favorite anime not found with id: {}", id);
            throw new AnimeNotFoundException(id.toString());
        }
    }

    public ResponseEntity<Void> deleteFavorite(UUID id) {
        log.info("Deleting favorite anime by id: {}", id);

        try {
            favoriteAnimeDatabaseService.findById(id);
            favoriteAnimeDatabaseService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (NoSuchElementException e) {
            log.error("Favorite anime not found with id: {}", id);
            throw new AnimeNotFoundException(id.toString());
        }
    }

    private AnimeSearchResponse mapToAnimeSearchResponse(JikanAnimeResponse response) {
        List<AnimeResponse> animeList =
                response.data() != null
                        ? response.data().stream().map(this::mapToAnimeResponse).toList()
                        : Collections.emptyList();

        return AnimeSearchResponse.builder()
                .data(animeList)
                .currentPage(
                        response.pagination() != null ? response.pagination().currentPage() : 1)
                .lastPage(
                        response.pagination() != null ? response.pagination().lastVisiblePage() : 1)
                .totalItems(
                        response.pagination() != null && response.pagination().items() != null
                                ? response.pagination().items().total()
                                : animeList.size())
                .hasNextPage(
                        response.pagination() != null ? response.pagination().hasNextPage() : false)
                .build();
    }

    private AnimeResponse mapToAnimeResponse(JikanAnimeResponse.AnimeData data) {
        return AnimeResponse.builder()
                .malId(data.malId())
                .title(data.title())
                .titleEnglish(data.titleEnglish())
                .titleJapanese(data.titleJapanese())
                .imageUrl(
                        data.images() != null && data.images().jpg() != null
                                ? data.images().jpg().imageUrl()
                                : null)
                .synopsis(data.synopsis())
                .score(data.score())
                .episodes(data.episodes())
                .status(data.status())
                .type(data.type())
                .rating(data.rating())
                .rank(data.rank())
                .popularity(data.popularity())
                .season(data.season())
                .year(data.year())
                .genres(
                        data.genres() != null
                                ? data.genres().stream()
                                        .map(JikanAnimeResponse.AnimeData.Genre::name)
                                        .toList()
                                : Collections.emptyList())
                .studios(
                        data.studios() != null
                                ? data.studios().stream()
                                        .map(JikanAnimeResponse.AnimeData.Studio::name)
                                        .toList()
                                : Collections.emptyList())
                .build();
    }

    private FavoriteAnimeResponse mapToFavoriteAnimeResponse(FavoriteAnime favorite) {
        return FavoriteAnimeResponse.builder()
                .id(favorite.getId())
                .malId(favorite.getMalId())
                .title(favorite.getTitle())
                .titleEnglish(favorite.getTitleEnglish())
                .imageUrl(favorite.getImageUrl())
                .synopsis(favorite.getSynopsis())
                .score(favorite.getScore())
                .episodes(favorite.getEpisodes())
                .status(favorite.getStatus())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
