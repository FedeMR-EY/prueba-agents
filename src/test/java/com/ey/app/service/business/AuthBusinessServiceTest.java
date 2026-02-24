package com.ey.app.service.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ey.app.controller.dto.request.RegisterUserRequest;
import com.ey.app.controller.dto.response.RegisterUserResponse;
import com.ey.app.exception.UserAlreadyExistsException;
import com.ey.app.model.entity.User;
import com.ey.app.service.UserDatabaseService;
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
class AuthBusinessServiceTest {

    @Mock
    private UserDatabaseService userDatabaseService;

    private AuthBusinessService authBusinessService;

    @BeforeEach
    void setUp() {
        authBusinessService = new AuthBusinessService(userDatabaseService);
    }

    @Test
    void registerUser_whenEmailNotExists_shouldCreateUser() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .build();

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .createdAt(LocalDateTime.now())
                .build();

        when(userDatabaseService.existsByEmail(request.email())).thenReturn(false);
        when(userDatabaseService.save(any(User.class))).thenReturn(savedUser);

        ResponseEntity<RegisterUserResponse> response = authBusinessService.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedUser.getId(), response.getBody().id());
        assertEquals(savedUser.getName(), response.getBody().name());
        assertEquals(savedUser.getEmail(), response.getBody().email());
        verify(userDatabaseService).save(any(User.class));
    }

    @Test
    void registerUser_whenEmailExists_shouldThrowException() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("John Doe")
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userDatabaseService.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authBusinessService.registerUser(request));
        verify(userDatabaseService, never()).save(any(User.class));
    }
}
