package com.ey.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ey.app.controller.dto.request.SaveFavoriteRequest;
import com.ey.app.controller.dto.response.AnimeResponse;
import com.ey.app.controller.dto.response.AnimeSearchResponse;
import com.ey.app.controller.dto.response.FavoriteAnimeResponse;
import com.ey.app.exception.GlobalExceptionHandler;
import com.ey.app.service.business.AnimeBusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AnimeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnimeBusinessService animeBusinessService;

    @InjectMocks
    private AnimeController animeController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(animeController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void searchAnime_ReturnsAnimeSearchResponse() throws Exception {
        AnimeResponse animeResponse =
                AnimeResponse.builder()
                        .malId(1L)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .score(8.0)
                        .episodes(220)
                        .status("Finished Airing")
                        .genres(List.of("Action", "Adventure"))
                        .studios(List.of("Pierrot"))
                        .build();

        AnimeSearchResponse searchResponse =
                AnimeSearchResponse.builder()
                        .data(List.of(animeResponse))
                        .currentPage(1)
                        .lastPage(1)
                        .totalItems(1)
                        .hasNextPage(false)
                        .build();

        when(animeBusinessService.searchAnime(eq("Naruto"), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok(searchResponse));

        mockMvc
                .perform(get("/v1/anime/search").param("q", "Naruto").param("page", "1").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Naruto"))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.hasNextPage").value(false));
    }

    @Test
    void getAnimeById_ReturnsAnimeResponse() throws Exception {
        AnimeResponse animeResponse =
                AnimeResponse.builder()
                        .malId(1L)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .score(8.0)
                        .episodes(220)
                        .status("Finished Airing")
                        .genres(List.of("Action"))
                        .studios(List.of("Pierrot"))
                        .build();

        when(animeBusinessService.getAnimeById(1L)).thenReturn(ResponseEntity.ok(animeResponse));

        mockMvc
                .perform(get("/v1/anime/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malId").value(1))
                .andExpect(jsonPath("$.title").value("Naruto"));
    }

    @Test
    void getTopAnime_ReturnsAnimeSearchResponse() throws Exception {
        AnimeSearchResponse searchResponse =
                AnimeSearchResponse.builder()
                        .data(Collections.emptyList())
                        .currentPage(1)
                        .lastPage(1)
                        .totalItems(0)
                        .hasNextPage(false)
                        .build();

        when(animeBusinessService.getTopAnime(eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok(searchResponse));

        mockMvc
                .perform(get("/v1/anime/top").param("page", "1").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    void saveFavorite_ReturnsFavoriteAnimeResponse() throws Exception {
        UUID id = UUID.randomUUID();
        FavoriteAnimeResponse favoriteResponse =
                FavoriteAnimeResponse.builder()
                        .id(id)
                        .malId(1L)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .score(8.0)
                        .episodes(220)
                        .status("Finished Airing")
                        .createdAt(LocalDateTime.now())
                        .build();

        when(animeBusinessService.saveFavorite(any(SaveFavoriteRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(favoriteResponse));

        SaveFavoriteRequest request = SaveFavoriteRequest.builder().malId(1L).build();

        mockMvc
                .perform(
                        post("/v1/anime/favorites")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.malId").value(1))
                .andExpect(jsonPath("$.title").value("Naruto"));
    }

    @Test
    void getAllFavorites_ReturnsFavoriteList() throws Exception {
        UUID id = UUID.randomUUID();
        FavoriteAnimeResponse favoriteResponse =
                FavoriteAnimeResponse.builder()
                        .id(id)
                        .malId(1L)
                        .title("Naruto")
                        .createdAt(LocalDateTime.now())
                        .build();

        when(animeBusinessService.getAllFavorites())
                .thenReturn(ResponseEntity.ok(List.of(favoriteResponse)));

        mockMvc
                .perform(get("/v1/anime/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].malId").value(1));
    }

    @Test
    void getFavoriteById_ReturnsFavoriteAnimeResponse() throws Exception {
        UUID id = UUID.randomUUID();
        FavoriteAnimeResponse favoriteResponse =
                FavoriteAnimeResponse.builder()
                        .id(id)
                        .malId(1L)
                        .title("Naruto")
                        .createdAt(LocalDateTime.now())
                        .build();

        when(animeBusinessService.getFavoriteById(id)).thenReturn(ResponseEntity.ok(favoriteResponse));

        mockMvc
                .perform(get("/v1/anime/favorites/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malId").value(1));
    }

    @Test
    void deleteFavorite_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        when(animeBusinessService.deleteFavorite(id)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/v1/anime/favorites/" + id)).andExpect(status().isNoContent());
    }
}
