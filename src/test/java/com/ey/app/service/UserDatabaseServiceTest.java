package com.ey.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ey.app.model.entity.User;
import com.ey.app.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDatabaseServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserDatabaseService userDatabaseService;

    @BeforeEach
    void setUp() {
        userDatabaseService = new UserDatabaseService(userRepository);
    }

    @Test
    void save_shouldSaveAndReturnUser() {
        User user = createUser();
        when(userRepository.save(user)).thenReturn(user);

        User result = userDatabaseService.save(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        List<User> users = List.of(createUser());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userDatabaseService.getAll();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        UUID id = UUID.randomUUID();
        User user = createUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userDatabaseService.findById(id);

        assertEquals(user, result);
    }

    @Test
    void findById_whenUserNotExists_shouldReturnNull() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        User result = userDatabaseService.findById(id);

        assertNull(result);
    }

    @Test
    void deleteById_shouldDeleteUser() {
        UUID id = UUID.randomUUID();

        userDatabaseService.deleteById(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void existsByEmail_shouldReturnTrue() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userDatabaseService.existsByEmail(email);

        assertTrue(result);
    }

    private User createUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
