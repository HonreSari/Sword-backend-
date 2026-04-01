package org.example.demo.Dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.example.demo.entity.Episode;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
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
