package com.ey.app.service;

import com.ey.app.model.entity.FavoriteAnime;
import com.ey.app.repository.FavoriteAnimeRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FavoriteAnimeDatabaseService implements DatabaseService<FavoriteAnime> {

    private final FavoriteAnimeRepository favoriteAnimeRepository;

    public FavoriteAnimeDatabaseService(FavoriteAnimeRepository favoriteAnimeRepository) {
        this.favoriteAnimeRepository = favoriteAnimeRepository;
    }

    @Override
    public FavoriteAnime save(FavoriteAnime entity) {
        log.info("Saving favorite anime with malId: {}", entity.getMalId());
        return favoriteAnimeRepository.save(entity);
    }

    @Override
    public List<FavoriteAnime> getAll() {
        log.info("Getting all favorite anime");
        return favoriteAnimeRepository.findAll();
    }

    @Override
    public FavoriteAnime findById(UUID id) {
        log.info("Finding favorite anime by id: {}", id);
        return favoriteAnimeRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Deleting favorite anime by id: {}", id);
        favoriteAnimeRepository.deleteById(id);
    }

    public FavoriteAnime findByMalId(Long malId) {
        log.info("Finding favorite anime by malId: {}", malId);
        return favoriteAnimeRepository.findByMalId(malId).orElseThrow(NoSuchElementException::new);
    }

    public boolean existsByMalId(Long malId) {
        log.info("Checking if favorite anime exists by malId: {}", malId);
        return favoriteAnimeRepository.existsByMalId(malId);
    }
}
