package com.ey.restaurante.service;

import java.util.List;
import java.util.Optional;

public interface DatabaseService<T> {

    T save(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
