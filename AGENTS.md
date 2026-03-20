# AGENTS.md - Anime Search API

Este proyecto sigue los lineamientos definidos en el repositorio `java-skills-agents`.

## Stack Tecnológico

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.2.5 |
| PostgreSQL | 16+ |
| Docker/Docker Compose | Local testing |
| Maven | Gestión de dependencias |
| OpenFeign | Llamadas HTTP declarativas |

## Estructura del Proyecto

```
java/com/ey/app/
├── clients/
│   ├── config/
│   │   └── GlobalFeignConfig.java
│   └── jikan/
│       ├── config/
│       │   └── JikanClientConfig.java
│       ├── dto/response/
│       │   ├── JikanAnimeByIdResponse.java
│       │   └── JikanAnimeResponse.java
│       └── JikanClient.java
├── config/
│   └── OpenApiConfig.java
├── controller/
│   ├── dto/request/
│   │   └── SaveFavoriteRequest.java
│   ├── dto/response/
│   │   ├── AnimeResponse.java
│   │   ├── AnimeSearchResponse.java
│   │   └── FavoriteAnimeResponse.java
│   ├── AnimeController.java
│   └── AnimeResource.java
├── exception/
│   ├── AnimeAlreadyFavoriteException.java
│   ├── AnimeNotFoundException.java
│   ├── ApiError.java
│   ├── ApiException.java
│   ├── ExternalApiException.java
│   └── GlobalExceptionHandler.java
├── model/entity/
│   └── FavoriteAnime.java
├── repository/
│   └── FavoriteAnimeRepository.java
├── service/
│   ├── business/
│   │   └── AnimeBusinessService.java
│   ├── DatabaseService.java
│   └── FavoriteAnimeDatabaseService.java
└── App.java
```

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /v1/anime/search?q={query} | Buscar anime por nombre |
| GET | /v1/anime/{malId} | Obtener anime por ID de MAL |
| GET | /v1/anime/top | Obtener ranking de anime |
| POST | /v1/anime/favorites | Guardar anime en favoritos |
| GET | /v1/anime/favorites | Listar favoritos |
| GET | /v1/anime/favorites/{id} | Obtener favorito por ID |
| DELETE | /v1/anime/favorites/{id} | Eliminar de favoritos |

## API Externa

Este proyecto consume la **Jikan API** (https://api.jikan.moe/v4) para obtener información de anime de MyAnimeList.

## Cómo Ejecutar

### Con Docker Compose

```bash
docker-compose up -d
```

### Solo Base de Datos

```bash
docker-compose up -d postgres
mvn spring-boot:run
```

### Swagger UI

Una vez iniciada la aplicación, acceder a: http://localhost:8080/swagger-ui.html
