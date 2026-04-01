package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.Dto.EpisodeStreamDTO;
import org.example.demo.entity.Episode;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.EpisodeRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EpisodeService {

  private final EpisodeRepo episodeRepository;

  @Cacheable(value = "episode:stream", key = "#id")
  public EpisodeStreamDTO getEpisodeForStreaming(Long id) {
    Episode episode =
        episodeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Episode", "id", id));
    return EpisodeStreamDTO.fromEntity(episode);
  }
}
