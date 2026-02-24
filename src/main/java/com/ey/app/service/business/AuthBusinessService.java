package com.ey.app.service.business;

import com.ey.app.controller.dto.request.RegisterUserRequest;
import com.ey.app.controller.dto.response.RegisterUserResponse;
import com.ey.app.exception.UserAlreadyExistsException;
import com.ey.app.model.entity.User;
import com.ey.app.service.UserDatabaseService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthBusinessService {

    private final UserDatabaseService userDatabaseService;

    public AuthBusinessService(UserDatabaseService userDatabaseService) {
        this.userDatabaseService = userDatabaseService;
    }

    public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest request) {
        log.info("Registering user with email: {}", request.email());

        if (userDatabaseService.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userDatabaseService.save(user);

        RegisterUserResponse response = RegisterUserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();

        log.info("User registered successfully with id: {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
