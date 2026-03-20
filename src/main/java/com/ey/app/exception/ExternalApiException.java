package com.ey.app.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class ExternalApiException extends ApiException {

    public ExternalApiException(String apiName, String reason) {
        super(
                "External API error",
                new ApiError(
                        "EXTERNAL_API_ERROR",
                        "Error communicating with external API",
                        Map.of("api", apiName, "reason", reason),
                        HttpStatus.SERVICE_UNAVAILABLE));
    }
}
