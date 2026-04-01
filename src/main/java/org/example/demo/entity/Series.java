package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Series {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title; // "My Senior Brother Is Too Steady" or "剑来"

  @Column(name = "chinese_title", length = 255)
  private String chineseTitle; // "师兄啊师兄" or "剑来"

  @Column(name = "cover_image_url", length = 500)
  private String coverImageUrl; // Main poster image (like in the screenshot)

  @Column(columnDefinition = "TEXT")
  private String description; // Short synopsis shown below title

  @Enumerated(EnumType.STRING)
  private ContentType type; // DONGHUA or WEBNOVEL

  public enum ContentType {
    DONGHUA,
    WEBNOVEL
  }

  @Column(name = "is_premium")
  private boolean isPremium = false; // For VIP-only series

  @Column(length = 20)
  private String status; // "Currently Airing", "Finished", "Upcoming"

  @Column(name = "aired_from")
  private String airedFrom; // e.g. "Dec 14, 2023"

  @Column(name = "aired_to")
  private String airedTo;

  @Column(length = 100)
  private String studio; // "Sparkly Key Animation Studio" or "Cloud Art"

  @Column(name = "duration", length = 50)
  private String duration; // "21m" per episode

  @Column
  private Double rating; // MAL Score style, e.g. 7.55

  @ElementCollection
  @CollectionTable(name = "series_genres", joinColumns = @JoinColumn(name = "series_id"))
  @Column(name = "genre")
  private List<String> genres = new ArrayList<>();

  @Column(name = "total_episodes")
  private Integer totalEpisodes;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Season> seasons = new HashSet<>();

  public void addSeason(Season season) {
    seasons.add(season);
    season.setSeries(this);
  }
}
