package org.example.demo.Dto;

import java.io.Serializable;
import org.example.demo.entity.Episode;

public record EpisodeListItemDTO(
    Long id,
    String title,
    String duration, // ✅ String like "24m" (matches entity)
    Integer episodeNumber
// ❌ Removed: thumbnailUrl, isPremium (entity doesn't have these)
) implements Serializable {
  private static final long serialVersionUID = 1L;
  public static EpisodeListItemDTO fromEntity(Episode episode) {
    return new EpisodeListItemDTO(
        episode.getId(),
        episode.getTitle(),
        episode.getDuration(),
        episode.getEpisodeNumber());
  }
}
