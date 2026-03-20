package com.ey.app.controller.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AnimeSearchResponse(
        List<AnimeResponse> data,
        Integer currentPage,
        Integer lastPage,
        Integer totalItems,
        Boolean hasNextPage) {}
