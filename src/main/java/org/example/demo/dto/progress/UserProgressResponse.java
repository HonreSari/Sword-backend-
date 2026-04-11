package org.example.demo.dto.progress;

import java.time.LocalDateTime;

public record UserProgressResponse(
    Long id,
    Long episodeId,
    String episodeTitle,
    Long seriesId,
    String seriesTitle,
    String coverImageUrl,
    Integer watchedDuration,
    Integer totalDuration,
    Boolean isCompleted,
    LocalDateTime lastWatchedAt) {
  // Factory method will be in Service layer
}
