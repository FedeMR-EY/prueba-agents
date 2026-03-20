package com.ey.app.clients.jikan.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record JikanAnimeResponse(List<AnimeData> data, Pagination pagination) {

    @Builder
    public record AnimeData(
            @JsonProperty("mal_id") Long malId,
            String url,
            Images images,
            String title,
            @JsonProperty("title_english") String titleEnglish,
            @JsonProperty("title_japanese") String titleJapanese,
            String type,
            String source,
            Integer episodes,
            String status,
            String duration,
            String rating,
            Double score,
            @JsonProperty("scored_by") Integer scoredBy,
            Integer rank,
            Integer popularity,
            Integer members,
            Integer favorites,
            String synopsis,
            String background,
            String season,
            Integer year,
            List<Genre> genres,
            List<Studio> studios) {

        @Builder
        public record Images(Jpg jpg) {

            @Builder
            public record Jpg(
                    @JsonProperty("image_url") String imageUrl,
                    @JsonProperty("small_image_url") String smallImageUrl,
                    @JsonProperty("large_image_url") String largeImageUrl) {}
        }

        @Builder
        public record Genre(@JsonProperty("mal_id") Long malId, String type, String name, String url) {}

        @Builder
        public record Studio(@JsonProperty("mal_id") Long malId, String type, String name, String url) {}
    }

    @Builder
    public record Pagination(
            @JsonProperty("last_visible_page") Integer lastVisiblePage,
            @JsonProperty("has_next_page") Boolean hasNextPage,
            @JsonProperty("current_page") Integer currentPage,
            Items items) {

        @Builder
        public record Items(Integer count, Integer total, @JsonProperty("per_page") Integer perPage) {}
    }
}
