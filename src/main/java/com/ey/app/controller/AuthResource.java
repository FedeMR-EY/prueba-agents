package com.ey.app.controller;

import com.ey.app.controller.dto.request.RegisterUserRequest;
import com.ey.app.controller.dto.response.RegisterUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "User authentication endpoints")
@RequestMapping("/v1/auth")
public interface AuthResource {

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    ResponseEntity<RegisterUserResponse> registerUser(@RequestBody @Valid RegisterUserRequest request);
}
