package com.ey.app.clients.jikan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ey.app.clients.jikan.dto.response.JikanAnimeByIdResponse;
import com.ey.app.clients.jikan.dto.response.JikanAnimeResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JikanClientTest {

    @Test
    void jikanAnimeResponse_CanBeBuilt() {
        JikanAnimeResponse.AnimeData.Images.Jpg jpg =
                JikanAnimeResponse.AnimeData.Images.Jpg.builder()
                        .imageUrl("https://example.com/image.jpg")
                        .smallImageUrl("https://example.com/small.jpg")
                        .largeImageUrl("https://example.com/large.jpg")
                        .build();

        JikanAnimeResponse.AnimeData.Images images =
                JikanAnimeResponse.AnimeData.Images.builder().jpg(jpg).build();

        JikanAnimeResponse.AnimeData.Genre genre =
                JikanAnimeResponse.AnimeData.Genre.builder()
                        .malId(1L)
                        .type("anime")
                        .name("Action")
                        .url("https://example.com/genre")
                        .build();

        JikanAnimeResponse.AnimeData.Studio studio =
                JikanAnimeResponse.AnimeData.Studio.builder()
                        .malId(1L)
                        .type("anime")
                        .name("Pierrot")
                        .url("https://example.com/studio")
                        .build();

        JikanAnimeResponse.AnimeData animeData =
                JikanAnimeResponse.AnimeData.builder()
                        .malId(1L)
                        .url("https://example.com")
                        .images(images)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .titleJapanese("ナルト")
                        .type("TV")
                        .source("Manga")
                        .episodes(220)
                        .status("Finished Airing")
                        .duration("23 min per ep")
                        .rating("PG-13")
                        .score(8.0)
                        .scoredBy(1000000)
                        .rank(100)
                        .popularity(50)
                        .members(2000000)
                        .favorites(100000)
                        .synopsis("A ninja story")
                        .background("Based on manga")
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
                        .lastVisiblePage(1)
                        .hasNextPage(false)
                        .currentPage(1)
                        .items(items)
                        .build();

        JikanAnimeResponse response =
                JikanAnimeResponse.builder()
                        .data(List.of(animeData))
                        .pagination(pagination)
                        .build();

        assertNotNull(response);
        assertEquals(1, response.data().size());
        assertEquals("Naruto", response.data().get(0).title());
        assertEquals(1, response.pagination().currentPage());
    }

    @Test
    void jikanAnimeByIdResponse_CanBeBuilt() {
        JikanAnimeResponse.AnimeData animeData =
                JikanAnimeResponse.AnimeData.builder()
                        .malId(1L)
                        .title("Naruto")
                        .build();

        JikanAnimeByIdResponse response =
                JikanAnimeByIdResponse.builder().data(animeData).build();

        assertNotNull(response);
        assertEquals("Naruto", response.data().title());
        assertEquals(1L, response.data().malId());
    }
}
