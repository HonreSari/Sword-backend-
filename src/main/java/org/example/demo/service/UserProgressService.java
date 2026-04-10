package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.progress.UserProgressRequest;
import org.example.demo.dto.progress.UserProgressResponse;
import org.example.demo.entity.Episode;
import org.example.demo.entity.User;
import org.example.demo.entity.UserProgress;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.EpisodeRepo;
import org.example.demo.repository.UserProgressRepository;
import org.example.demo.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProgressService {

  private final UserProgressRepository progressRepository;
  private final UserRepository userRepository;
  private final EpisodeRepo episodeRepository;

  @Transactional
  @CacheEvict(value = "user:progress", key = "#username")
  public UserProgressResponse saveProgress(String username, UserProgressRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    Episode episode = episodeRepository.findById(request.episodeId())
        .orElseThrow(() -> new ResourceNotFoundException("Episode", "id", request.episodeId()));

    // ✅ Upsert: find existing or create new
    UserProgress progress = progressRepository
        .findByUserIdAndEpisodeId(user.getId(), episode.getId())
        .orElse(new UserProgress(null, user, episode, null, false, null, null, null));

    // ✅ Update fields
    progress.setWatchedDuration(request.watchedDuration());
    progress.setCompleted(Boolean.TRUE.equals(request.isCompleted()));
    progress.setLastWatchedAt(LocalDateTime.now());

    progressRepository.save(progress);

    // ✅ Return response
    return toResponse(progress, episode);
  }

  // ✅ Get all progress for user (cached)
  @Cacheable(value = "user:progress", key = "#username")
  @Transactional(readOnly = true)
  public List<UserProgressResponse> getProgressByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    return progressRepository.findByUserIdWithDetails(user.getId())
        .stream()
        .map(p -> toResponse(p, p.getEpisode()))
        .toList();
  }

  // ✅ Helper: Entity → Response DTO
  private UserProgressResponse toResponse(UserProgress progress, Episode episode) {
    return new UserProgressResponse(
        progress.getId(),
        episode.getId(),
        episode.getTitle(),
        episode.getSeason().getSeries().getId(),
        episode.getSeason().getSeries().getTitle(),
        episode.getSeason().getSeries().getCoverImageUrl(),
        progress.getWatchedDuration(),
        parseDurationToSeconds(episode.getDuration()),
        progress.isCompleted(),
        progress.getLastWatchedAt());
  }

  // ✅ Helper: Parse "24m" → 1440 seconds
  private Integer parseDurationToSeconds(String duration) {
    if (duration == null || duration.isEmpty())
      return 0;
    try {
      if (duration.endsWith("m")) {
        return Integer.parseInt(duration.replace("m", "").trim()) * 60;
      }
      return Integer.parseInt(duration);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
