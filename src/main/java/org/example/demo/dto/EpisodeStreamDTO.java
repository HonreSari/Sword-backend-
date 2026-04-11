// src/main/java/org/example/demo/dto/EpisodeStreamDTO.java

package org.example.demo.dto;

import org.example.demo.entity.Episode;

public record EpisodeStreamDTO(
    // ✅ Core episode fields
    Long id,
    String title,
    String videoUrl, // YouTube embed URL
    String duration,
    Integer episodeNumber,

    // ✅ Context for display (from my version)
    Long seriesId,
    String seriesTitle,
    String coverImageUrl,
    Long seasonId,
    Integer seasonOrder,

    // ✅ Navigation for auto-play (from your version)
    Long nextEpisodeId,
    Long prevEpisodeId) {
  public static EpisodeStreamDTO fromEntity(Episode episode) {
    return new EpisodeStreamDTO(
        episode.getId(),
        episode.getTitle(),
        episode.getVideoUrl(),
        episode.getDuration(),
        episode.getEpisodeNumber(),
        // Context fields
        episode.getSeason().getSeries().getId(),
        episode.getSeason().getSeries().getTitle(),
        episode.getSeason().getSeries().getCoverImageUrl(),
        episode.getSeason().getId(),
        episode.getSeason().getSeasonOrder(),
        // Navigation: null for now (can compute in Service if needed)
        null,
        null);
  }
}
