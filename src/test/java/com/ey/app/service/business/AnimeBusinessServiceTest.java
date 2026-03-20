package com.ey.app.service.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import feign.Request;
import feign.RequestTemplate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AnimeBusinessServiceTest {

    @Mock
    private JikanClient jikanClient;

    @Mock
    private FavoriteAnimeDatabaseService favoriteAnimeDatabaseService;

    @InjectMocks
    private AnimeBusinessService animeBusinessService;

    private JikanAnimeResponse.AnimeData animeData;
    private JikanAnimeResponse jikanAnimeResponse;
    private JikanAnimeByIdResponse jikanAnimeByIdResponse;
    private FavoriteAnime favoriteAnime;

    @BeforeEach
    void setUp() {
        JikanAnimeResponse.AnimeData.Images.Jpg jpg =
                JikanAnimeResponse.AnimeData.Images.Jpg.builder()
                        .imageUrl("https://example.com/image.jpg")
                        .build();

        JikanAnimeResponse.AnimeData.Images images =
                JikanAnimeResponse.AnimeData.Images.builder().jpg(jpg).build();

        JikanAnimeResponse.AnimeData.Genre genre =
                JikanAnimeResponse.AnimeData.Genre.builder()
                        .malId(1L)
                        .name("Action")
                        .type("anime")
                        .build();

        JikanAnimeResponse.AnimeData.Studio studio =
                JikanAnimeResponse.AnimeData.Studio.builder()
                        .malId(1L)
                        .name("Pierrot")
                        .type("anime")
                        .build();

        animeData =
                JikanAnimeResponse.AnimeData.builder()
                        .malId(1L)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .titleJapanese("ナルト")
                        .images(images)
                        .synopsis("A ninja story")
                        .score(8.0)
                        .episodes(220)
                        .status("Finished Airing")
                        .type("TV")
                        .rating("PG-13")
                        .rank(100)
                        .popularity(50)
                        .season("Fall")
                        .year(2002)
                        .genres(List.of(genre))
                        .studios(List.of(studio))
                        .build();

        JikanAnimeResponse.Pagination.Items items =
                JikanAnimeResponse.Pagination.Items.builder()
                        .count(1)
                        .total(1)
                        .perPage(10)
                        .build();

        JikanAnimeResponse.Pagination pagination =
                JikanAnimeResponse.Pagination.builder()
                        .currentPage(1)
                        .lastVisiblePage(1)
                        .hasNextPage(false)
                        .items(items)
                        .build();

        jikanAnimeResponse =
                JikanAnimeResponse.builder()
                        .data(List.of(animeData))
                        .pagination(pagination)
                        .build();

        jikanAnimeByIdResponse = JikanAnimeByIdResponse.builder().data(animeData).build();

        favoriteAnime =
                FavoriteAnime.builder()
                        .id(UUID.randomUUID())
                        .malId(1L)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .imageUrl("https://example.com/image.jpg")
                        .synopsis("A ninja story")
                        .score(8.0)
                        .episodes(220)
                        .status("Finished Airing")
                        .createdAt(LocalDateTime.now())
                        .build();
    }

    @Test
    void searchAnime_ReturnsAnimeSearchResponse() {
        when(jikanClient.searchAnime(eq("Naruto"), eq(1), eq(10))).thenReturn(jikanAnimeResponse);

        ResponseEntity<AnimeSearchResponse> response =
                animeBusinessService.searchAnime("Naruto", 1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().data().size());
        assertEquals("Naruto", response.getBody().data().get(0).title());
    }

    @Test
    void searchAnime_ThrowsExternalApiException_WhenFeignFails() {
        Request request =
                Request.create(
                        Request.HttpMethod.GET,
                        "http://test",
                        new HashMap<>(),
                        null,
                        new RequestTemplate());
        when(jikanClient.searchAnime(eq("Naruto"), eq(1), eq(10)))
                .thenThrow(new FeignException.ServiceUnavailable("Error", request, null, null));

        assertThrows(
                ExternalApiException.class, () -> animeBusinessService.searchAnime("Naruto", 1, 10));
    }

    @Test
    void getAnimeById_ReturnsAnimeResponse() {
        when(jikanClient.getAnimeById(1L)).thenReturn(jikanAnimeByIdResponse);

        ResponseEntity<AnimeResponse> response = animeBusinessService.getAnimeById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Naruto", response.getBody().title());
        assertEquals(1L, response.getBody().malId());
    }

    @Test
    void getAnimeById_ThrowsAnimeNotFoundException_WhenDataIsNull() {
        JikanAnimeByIdResponse emptyResponse = JikanAnimeByIdResponse.builder().data(null).build();
        when(jikanClient.getAnimeById(1L)).thenReturn(emptyResponse);

        assertThrows(AnimeNotFoundException.class, () -> animeBusinessService.getAnimeById(1L));
    }

    @Test
    void getAnimeById_ThrowsAnimeNotFoundException_WhenNotFound() {
        Request request =
                Request.create(
                        Request.HttpMethod.GET,
                        "http://test",
                        new HashMap<>(),
                        null,
                        new RequestTemplate());
        when(jikanClient.getAnimeById(1L))
                .thenThrow(new FeignException.NotFound("Not found", request, null, null));

        assertThrows(AnimeNotFoundException.class, () -> animeBusinessService.getAnimeById(1L));
    }

    @Test
    void getTopAnime_ReturnsAnimeSearchResponse() {
        when(jikanClient.getTopAnime(eq(1), eq(10))).thenReturn(jikanAnimeResponse);

        ResponseEntity<AnimeSearchResponse> response = animeBusinessService.getTopAnime(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().data().size());
    }

    @Test
    void saveFavorite_ReturnsFavoriteAnimeResponse() {
        SaveFavoriteRequest request = SaveFavoriteRequest.builder().malId(1L).build();

        when(favoriteAnimeDatabaseService.existsByMalId(1L)).thenReturn(false);
        when(jikanClient.getAnimeById(1L)).thenReturn(jikanAnimeByIdResponse);
        when(favoriteAnimeDatabaseService.save(any(FavoriteAnime.class))).thenReturn(favoriteAnime);

        ResponseEntity<FavoriteAnimeResponse> response = animeBusinessService.saveFavorite(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().malId());
        assertEquals("Naruto", response.getBody().title());
    }

    @Test
    void saveFavorite_ThrowsAnimeAlreadyFavoriteException_WhenExists() {
        SaveFavoriteRequest request = SaveFavoriteRequest.builder().malId(1L).build();

        when(favoriteAnimeDatabaseService.existsByMalId(1L)).thenReturn(true);

        assertThrows(
                AnimeAlreadyFavoriteException.class, () -> animeBusinessService.saveFavorite(request));
    }

    @Test
    void getAllFavorites_ReturnsFavoriteList() {
        when(favoriteAnimeDatabaseService.getAll()).thenReturn(List.of(favoriteAnime));

        ResponseEntity<List<FavoriteAnimeResponse>> response = animeBusinessService.getAllFavorites();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).malId());
    }

    @Test
    void getAllFavorites_ReturnsEmptyList_WhenNoFavorites() {
        when(favoriteAnimeDatabaseService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<FavoriteAnimeResponse>> response = animeBusinessService.getAllFavorites();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void getFavoriteById_ReturnsFavoriteAnimeResponse() {
        UUID id = favoriteAnime.getId();
        when(favoriteAnimeDatabaseService.findById(id)).thenReturn(favoriteAnime);

        ResponseEntity<FavoriteAnimeResponse> response = animeBusinessService.getFavoriteById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().malId());
    }

    @Test
    void getFavoriteById_ThrowsAnimeNotFoundException_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(favoriteAnimeDatabaseService.findById(id)).thenThrow(new NoSuchElementException());

        assertThrows(AnimeNotFoundException.class, () -> animeBusinessService.getFavoriteById(id));
    }

    @Test
    void deleteFavorite_ReturnsNoContent() {
        UUID id = favoriteAnime.getId();
        when(favoriteAnimeDatabaseService.findById(id)).thenReturn(favoriteAnime);

        ResponseEntity<Void> response = animeBusinessService.deleteFavorite(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(favoriteAnimeDatabaseService, times(1)).deleteById(id);
    }

    @Test
    void deleteFavorite_ThrowsAnimeNotFoundException_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(favoriteAnimeDatabaseService.findById(id)).thenThrow(new NoSuchElementException());

        assertThrows(AnimeNotFoundException.class, () -> animeBusinessService.deleteFavorite(id));
    }
}
