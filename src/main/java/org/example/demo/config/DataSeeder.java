package org.example.demo.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.demo.entity.Episode;
import org.example.demo.entity.Season;
import org.example.demo.entity.Series;
import org.example.demo.repository.SeriesRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final SeriesRepo seriesRepository;

  @Override
  public void run(String... args) throws Exception {
    if (seriesRepository.count() > 0) {
      System.out.println("✅ Database already seeded. Skipping...");
      return;
    }

    System.out.println("🌱 Starting data seeding...");

    ObjectMapper objectMapper = new ObjectMapper();
    InputStream inputStream = new ClassPathResource("data/donghua-list.json").getInputStream();
    JsonNode rootNode = objectMapper.readTree(inputStream);

    int count = 0;
    for (JsonNode node : rootNode) {
      String title = getNodeText(node, "A ");

      // Skip header row
      if ("Title (EN)".equals(title)) continue;

      try {
        Series series = convertToEntity(node);
        seriesRepository.save(series);
        count++;
        System.out.println("✅ Seeded: " + title);
      } catch (Exception e) {
        System.err.println("❌ Failed to seed: " + title + " - " + e.getMessage());
      }
    }
    System.out.println("🎉 Successfully seeded " + count + " donghua!");
  }

  private Series convertToEntity(JsonNode node) {
    Series series = new Series();

    series.setTitle(getNodeText(node, "A "));
    series.setChineseTitle(getNodeText(node, "B "));
    series.setCoverImageUrl(getNodeText(node, "C "));

    // Parse Rating
    try {
      series.setRating(Double.parseDouble(getNodeText(node, "E ")));
    } catch (NumberFormatException e) {
      series.setRating(0.0);
    }

    series.setStatus(getNodeText(node, "F "));

    // Parse Total Episodes
    try {
      series.setTotalEpisodes(Integer.parseInt(getNodeText(node, "G ")));
    } catch (NumberFormatException e) {
      series.setTotalEpisodes(0);
    }

    // Parse Genres
    String genresStr = getNodeText(node, "H ");
    List<String> genres = Arrays.stream(genresStr.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    series.setGenres(genres);

    // --- Create Season & Episode ---
    Season season = new Season();
    season.setSeasonName("Season 1");
    season.setSeasonOrder(1);
    season.setSeries(series);

    Episode episode = new Episode();
    episode.setTitle("Episode 1");
    episode.setEpisodeNumber(1);
    episode.setDuration("24m");

    // ✅ Use the full URL directly from Column D
    String videoUrl = getNodeText(node, "D ");
    episode.setVideoUrl(videoUrl);

    episode.setSeason(season);

    Set<Episode> episodeSet = new HashSet<>();
    episodeSet.add(episode);
    season.setEpisodes(episodeSet);

    Set<Season> seasonSet = new HashSet<>();
    seasonSet.add(season);
    series.setSeasons(seasonSet);

    return series;
  }

  private String getNodeText(JsonNode node, String key) {
    JsonNode field = node.get(key);
    if (field != null && field.isTextual()) {
      return field.asText().trim();
    }
    return "";
  }
}