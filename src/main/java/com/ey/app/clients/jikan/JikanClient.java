package com.ey.app.clients.jikan;

import com.ey.app.clients.jikan.config.JikanClientConfig;
import com.ey.app.clients.jikan.dto.response.JikanAnimeByIdResponse;
import com.ey.app.clients.jikan.dto.response.JikanAnimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "jikanClient",
        url = "${clients.jikan.url}",
        configuration = JikanClientConfig.class)
public interface JikanClient {

    @GetMapping(value = "/anime", produces = "application/json")
    JikanAnimeResponse searchAnime(
            @RequestParam("q") String query,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit);

    @GetMapping(value = "/anime/{id}", produces = "application/json")
    JikanAnimeByIdResponse getAnimeById(@PathVariable("id") Long id);

    @GetMapping(value = "/top/anime", produces = "application/json")
    JikanAnimeResponse getTopAnime(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit);
}
