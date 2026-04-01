package org.example.demo.Dto;

import org.example.demo.entity.Episode;

public record EpisodeStreamDTO(
    Long id,
    String title,
    String videoUrl, // ✅ The actual video link
    String duration,
    Integer episodeNumber,
    Long nextEpisodeId, // ✅ Optional: for auto-play next
    Long prevEpisodeId // ✅ Optional: for previous button
    ) {
  public static EpisodeStreamDTO fromEntity(Episode episode) {
    return new EpisodeStreamDTO(
        episode.getId(),
        episode.getTitle(),
        episode.getVideoUrl(), // ✅ Direct video URL
        episode.getDuration(),
        episode.getEpisodeNumber(),
        null, // nextEpisodeId: compute in Service layer if needed
        null // prevEpisodeId: compute in Service layer if needed
        );
  }
}
