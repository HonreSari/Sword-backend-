package org.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "user_progress")
public class UserProgress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "series_id", nullable = false)
  private Series series;

  @ManyToOne
  @JoinColumn(name = "last_episode_id") // Nullable if it's a novel
  private Episode lastEpisode;

  @ManyToOne
  @JoinColumn(name = "last_chapter_id") // Nullable if it's a donghua
  private Chapter lastChapter;

  private LocalDateTime updatedAt = LocalDateTime.now();
}
