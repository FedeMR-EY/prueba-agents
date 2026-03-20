package com.ey.app.controller;

import com.ey.app.controller.dto.request.SaveFavoriteRequest;
import com.ey.app.controller.dto.response.AnimeResponse;
import com.ey.app.controller.dto.response.AnimeSearchResponse;
import com.ey.app.controller.dto.response.FavoriteAnimeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/v1/anime")
@Tag(name = "Anime", description = "Endpoints para buscar y gestionar anime")
public interface AnimeResource {

    @GetMapping(value = "/search", produces = "application/json")
    @Operation(summary = "Buscar anime", description = "Busca anime por nombre usando la API de Jikan")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Busqueda exitosa"),
                @ApiResponse(responseCode = "503", description = "Error en la API externa")
            })
    ResponseEntity<AnimeSearchResponse> searchAnime(
            @Parameter(description = "Termino de busqueda") @RequestParam("q") String query,
            @Parameter(description = "Numero de pagina") @RequestParam(value = "page", required = false, defaultValue = "1")
                    Integer page,
            @Parameter(description = "Limite de resultados") @RequestParam(value = "limit", required = false, defaultValue = "10")
                    Integer limit);

    @GetMapping(value = "/{malId}", produces = "application/json")
    @Operation(
            summary = "Obtener anime por ID",
            description = "Obtiene los detalles de un anime por su ID de MyAnimeList")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Anime encontrado"),
                @ApiResponse(responseCode = "404", description = "Anime no encontrado"),
                @ApiResponse(responseCode = "503", description = "Error en la API externa")
            })
    ResponseEntity<AnimeResponse> getAnimeById(
            @Parameter(description = "ID de MyAnimeList") @PathVariable("malId") Long malId);

    @GetMapping(value = "/top", produces = "application/json")
    @Operation(summary = "Obtener top anime", description = "Obtiene el ranking de los mejores anime")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
                @ApiResponse(responseCode = "503", description = "Error en la API externa")
            })
    ResponseEntity<AnimeSearchResponse> getTopAnime(
            @Parameter(description = "Numero de pagina") @RequestParam(value = "page", required = false, defaultValue = "1")
                    Integer page,
            @Parameter(description = "Limite de resultados") @RequestParam(value = "limit", required = false, defaultValue = "10")
                    Integer limit);

    @PostMapping(value = "/favorites", produces = "application/json", consumes = "application/json")
    @Operation(
            summary = "Guardar anime favorito",
            description = "Guarda un anime en la lista de favoritos")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Anime guardado exitosamente"),
                @ApiResponse(responseCode = "404", description = "Anime no encontrado"),
                @ApiResponse(responseCode = "409", description = "Anime ya existe en favoritos"),
                @ApiResponse(responseCode = "503", description = "Error en la API externa")
            })
    ResponseEntity<FavoriteAnimeResponse> saveFavorite(
            @RequestBody @Valid SaveFavoriteRequest request);

    @GetMapping(value = "/favorites", produces = "application/json")
    @Operation(
            summary = "Obtener favoritos",
            description = "Obtiene la lista de todos los anime favoritos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    ResponseEntity<List<FavoriteAnimeResponse>> getAllFavorites();

    @GetMapping(value = "/favorites/{id}", produces = "application/json")
    @Operation(
            summary = "Obtener favorito por ID",
            description = "Obtiene un anime favorito por su ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Anime encontrado"),
                @ApiResponse(responseCode = "404", description = "Anime no encontrado")
            })
    ResponseEntity<FavoriteAnimeResponse> getFavoriteById(
            @Parameter(description = "ID del favorito") @PathVariable("id") UUID id);

    @DeleteMapping(value = "/favorites/{id}")
    @Operation(
            summary = "Eliminar favorito",
            description = "Elimina un anime de la lista de favoritos")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Anime eliminado exitosamente"),
                @ApiResponse(responseCode = "404", description = "Anime no encontrado")
            })
    ResponseEntity<Void> deleteFavorite(
            @Parameter(description = "ID del favorito") @PathVariable("id") UUID id);
}
