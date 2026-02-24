package com.ey.app.service;

import com.ey.app.model.entity.User;
import com.ey.app.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDatabaseService implements DatabaseService<User> {

    private final UserRepository userRepository;

    public UserDatabaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User entity) {
        log.info("Saving user with email: {}", entity.getEmail());
        return userRepository.save(entity);
    }

    @Override
    public List<User> getAll() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User findById(UUID id) {
        log.info("Finding user by id: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting user by id: {}", id);
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
