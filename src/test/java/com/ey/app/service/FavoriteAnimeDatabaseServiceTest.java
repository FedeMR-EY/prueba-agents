package com.ey.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ey.app.model.entity.FavoriteAnime;
import com.ey.app.repository.FavoriteAnimeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FavoriteAnimeDatabaseServiceTest {

    @Mock
    private FavoriteAnimeRepository favoriteAnimeRepository;

    @InjectMocks
    private FavoriteAnimeDatabaseService favoriteAnimeDatabaseService;

    private FavoriteAnime favoriteAnime;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        favoriteAnime =
                FavoriteAnime.builder()
                        .id(id)
                        .malId(1L)
                        .title("Naruto")
                        .titleEnglish("Naruto")
                        .score(8.0)
                        .episodes(220)
                        .status("Finished Airing")
                        .createdAt(LocalDateTime.now())
                        .build();
    }

    @Test
    void save_ReturnsSavedEntity() {
        when(favoriteAnimeRepository.save(any(FavoriteAnime.class))).thenReturn(favoriteAnime);

        FavoriteAnime result = favoriteAnimeDatabaseService.save(favoriteAnime);

        assertEquals(favoriteAnime.getMalId(), result.getMalId());
        assertEquals(favoriteAnime.getTitle(), result.getTitle());
        verify(favoriteAnimeRepository, times(1)).save(favoriteAnime);
    }

    @Test
    void getAll_ReturnsAllEntities() {
        when(favoriteAnimeRepository.findAll()).thenReturn(List.of(favoriteAnime));

        List<FavoriteAnime> result = favoriteAnimeDatabaseService.getAll();

        assertEquals(1, result.size());
        assertEquals(favoriteAnime.getMalId(), result.get(0).getMalId());
        verify(favoriteAnimeRepository, times(1)).findAll();
    }

    @Test
    void findById_ReturnsEntity_WhenExists() {
        when(favoriteAnimeRepository.findById(id)).thenReturn(Optional.of(favoriteAnime));

        FavoriteAnime result = favoriteAnimeDatabaseService.findById(id);

        assertEquals(favoriteAnime.getMalId(), result.getMalId());
        verify(favoriteAnimeRepository, times(1)).findById(id);
    }

    @Test
    void findById_ThrowsException_WhenNotExists() {
        when(favoriteAnimeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> favoriteAnimeDatabaseService.findById(id));
        verify(favoriteAnimeRepository, times(1)).findById(id);
    }

    @Test
    void deleteById_DeletesEntity() {
        favoriteAnimeDatabaseService.deleteById(id);

        verify(favoriteAnimeRepository, times(1)).deleteById(id);
    }

    @Test
    void findByMalId_ReturnsEntity_WhenExists() {
        when(favoriteAnimeRepository.findByMalId(1L)).thenReturn(Optional.of(favoriteAnime));

        FavoriteAnime result = favoriteAnimeDatabaseService.findByMalId(1L);

        assertEquals(favoriteAnime.getMalId(), result.getMalId());
        verify(favoriteAnimeRepository, times(1)).findByMalId(1L);
    }

    @Test
    void findByMalId_ThrowsException_WhenNotExists() {
        when(favoriteAnimeRepository.findByMalId(1L)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class, () -> favoriteAnimeDatabaseService.findByMalId(1L));
        verify(favoriteAnimeRepository, times(1)).findByMalId(1L);
    }

    @Test
    void existsByMalId_ReturnsTrue_WhenExists() {
        when(favoriteAnimeRepository.existsByMalId(1L)).thenReturn(true);

        boolean result = favoriteAnimeDatabaseService.existsByMalId(1L);

        assertTrue(result);
        verify(favoriteAnimeRepository, times(1)).existsByMalId(1L);
    }

    @Test
    void existsByMalId_ReturnsFalse_WhenNotExists() {
        when(favoriteAnimeRepository.existsByMalId(1L)).thenReturn(false);

        boolean result = favoriteAnimeDatabaseService.existsByMalId(1L);

        assertFalse(result);
        verify(favoriteAnimeRepository, times(1)).existsByMalId(1L);
    }
}
