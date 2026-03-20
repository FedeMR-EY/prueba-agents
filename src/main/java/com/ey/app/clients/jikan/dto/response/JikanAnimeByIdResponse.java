package com.ey.app.clients.jikan.dto.response;

import lombok.Builder;

@Builder
public record JikanAnimeByIdResponse(JikanAnimeResponse.AnimeData data) {}
