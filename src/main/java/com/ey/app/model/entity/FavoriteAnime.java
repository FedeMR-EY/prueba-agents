package com.ey.app.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorite_anime")
@Getter
@Setter
@Builder
public class FavoriteAnime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "mal_id", nullable = false, unique = true)
    private Long malId;

    @Column(nullable = false)
    private String title;

    @Column(name = "title_english")
    private String titleEnglish;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    private Double score;

    private Integer episodes;

    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public FavoriteAnime() {}

    public FavoriteAnime(
            UUID id,
            Long malId,
            String title,
            String titleEnglish,
            String imageUrl,
            String synopsis,
            Double score,
            Integer episodes,
            String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.malId = malId;
        this.title = title;
        this.titleEnglish = titleEnglish;
        this.imageUrl = imageUrl;
        this.synopsis = synopsis;
        this.score = score;
        this.episodes = episodes;
        this.status = status;
        this.createdAt = createdAt;
    }
}
