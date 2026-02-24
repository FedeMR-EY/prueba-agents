package com.ey.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ey.app.controller.dto.request.RegisterUserRequest;
import com.ey.app.controller.dto.response.RegisterUserResponse;
import com.ey.app.service.business.AuthBusinessService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthBusinessService authBusinessService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authBusinessService);
    }

    @Test
    void registerUser_shouldDelegateToBusinessService() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .build();

        RegisterUserResponse response = RegisterUserResponse.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(authBusinessService.registerUser(request))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(response));

        ResponseEntity<RegisterUserResponse> result = authController.registerUser(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(authBusinessService).registerUser(request);
    }
}
