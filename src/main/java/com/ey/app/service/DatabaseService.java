package com.ey.app.service;

import java.util.List;
import java.util.UUID;

public interface DatabaseService<T> {

    T save(T entity);

    List<T> getAll();

    T findById(UUID id);

    void deleteById(UUID id);
}
