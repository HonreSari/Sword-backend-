package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.EpisodeStreamDTO;
import org.example.demo.entity.Episode;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.EpisodeRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// src/main/java/org/example/demo/service/EpisodeService.java

@Service
@RequiredArgsConstructor
public class EpisodeService {

  private final EpisodeRepo episodeRepository;

  @Cacheable(value = "episode:stream", key = "#id")
  @Transactional(readOnly = true)
  public EpisodeStreamDTO getEpisodeForStreaming(Long id) {
    Episode episode =
        episodeRepository
            .findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Episode", "id", id));

    // ✅ Compute next/prev episode IDs
    Long nextId = findNextEpisodeId(episode);
    Long prevId = findPrevEpisodeId(episode);

    return new EpisodeStreamDTO(
        episode.getId(),
        episode.getTitle(),
        episode.getVideoUrl(),
        episode.getDuration(),
        episode.getEpisodeNumber(),
        episode.getSeason().getSeries().getId(),
        episode.getSeason().getSeries().getTitle(),
        episode.getSeason().getSeries().getCoverImageUrl(),
        episode.getSeason().getId(),
        episode.getSeason().getSeasonOrder(),
        nextId,
        prevId);
  }

  // Helper: Find next episode in same season
  private Long findNextEpisodeId(Episode current) {
    return episodeRepository
        .findBySeasonIdAndEpisodeNumberGreaterThan(
            current.getSeason().getId(), current.getEpisodeNumber())
        .stream()
        .min((e1, e2) -> e1.getEpisodeNumber().compareTo(e2.getEpisodeNumber()))
        .map(Episode::getId)
        .orElse(null);
  }

  // Helper: Find prev episode in same season
  private Long findPrevEpisodeId(Episode current) {
    return episodeRepository
        .findBySeasonIdAndEpisodeNumberLessThan(
            current.getSeason().getId(), current.getEpisodeNumber())
        .stream()
        .max((e1, e2) -> e1.getEpisodeNumber().compareTo(e2.getEpisodeNumber()))
        .map(Episode::getId)
        .orElse(null);
  }
}
