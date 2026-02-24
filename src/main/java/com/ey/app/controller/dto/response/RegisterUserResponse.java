package com.ey.app.controller.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegisterUserResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {}
