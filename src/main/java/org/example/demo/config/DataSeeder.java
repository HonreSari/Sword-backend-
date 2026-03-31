package org.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.example.demo.entity.*;
import org.example.demo.repository.SeriesRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final SeriesRepo seriesRepository;

  @Override
  public void run(String... args) {
    if (seriesRepository.count() > 0)
      return; // ✅ Don't seed if data exists

    // ✅ Create Mock Series 1
    Series s1 = new Series();
    s1.setTitle("Soul Land");
    s1.setChineseTitle("斗罗大陆");
    s1.setCoverImageUrl("https://via.placeholder.com/300x400?text=Soul+Land");
    s1.setDescription("Tang San reborn in a world of martial souls...");
    s1.setType(Series.ContentType.DONGHUA);
    s1.setStatus("Ongoing");
    s1.setRating(9.5);
    s1.setGenres(List.of("Action", "Fantasy"));
    s1.setTotalEpisodes(100);
    s1.setCreatedAt(LocalDateTime.now());

    // ✅ Add Season 1
    Season season1 = new Season();
    season1.setSeasonName("Season 1");
    season1.setSeasonOrder(1);
    season1.setSeries(s1);

    // ✅ Add Episode 1
    Episode ep1 = new Episode();
    ep1.setTitle("Episode 1: Rebirth");
    ep1.setEpisodeNumber(1);
    ep1.setDuration("24m");
    ep1.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4"); // ✅ Sample video
    season1.addEpisode(ep1);

    s1.addSeason(season1);
    seriesRepository.save(s1);

    System.out.println("✅ Seed data created!");
  }
}
