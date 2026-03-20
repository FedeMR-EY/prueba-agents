package com.ey.app.repository;

import com.ey.app.model.entity.FavoriteAnime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteAnimeRepository extends JpaRepository<FavoriteAnime, UUID> {

    Optional<FavoriteAnime> findByMalId(Long malId);

    boolean existsByMalId(Long malId);
}
