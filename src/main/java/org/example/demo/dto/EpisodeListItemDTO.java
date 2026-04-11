package org.example.demo.dto;

import org.example.demo.entity.Episode;

public record EpisodeListItemDTO(
    Long id,
    String title,
    String duration,
    Integer episodeNumber) {
  public static EpisodeListItemDTO fromEntity(Episode episode) {
    return new EpisodeListItemDTO(
        episode.getId(),
        episode.getTitle(),
        episode.getDuration(),
        episode.getEpisodeNumber()
    // ✅ No collections in this DTO, so no fix needed
    );
  }
}
