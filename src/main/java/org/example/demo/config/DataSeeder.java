package org.example.demo.config;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.example.demo.entity.Episode;
import org.example.demo.entity.Season;
import org.example.demo.entity.Series;
import org.example.demo.repository.SeriesRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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

    System.out.println("🌱 Starting data seeding from donghua-list.json...");

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
        e.printStackTrace();
      }
    }
    System.out.println("🎉 Successfully seeded " + count + " donghua!");
  }

  private Series convertToEntity(JsonNode node) {
    Series series = new Series();

    // Extract data using exact keys from JSON (note trailing spaces)
    series.setTitle(getNodeText(node, "A "));
    series.setChineseTitle(getNodeText(node, "B "));
    series.setCoverImageUrl(getNodeText(node, "C "));
    series.setDescription(getNodeText(node, "I ")); // ✅ NEW: Description from column I

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

    // Parse Genres (Split by comma)
    String genresStr = getNodeText(node, "H ");
    List<String> genres =
        Arrays.stream(genresStr.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
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

    // ✅ Handle Full YouTube URL from column D
    String youtubeUrl = getNodeText(node, "D ");
    episode.setVideoUrl(convertToEmbedUrl(youtubeUrl));

    episode.setSeason(season);

    // ✅ Use HashSet to match Set<Episode> in Entity
    Set<Episode> episodeSet = new HashSet<>();
    episodeSet.add(episode);
    season.setEpisodes(episodeSet);

    // ✅ Use HashSet to match Set<Season> in Entity
    Set<Season> seasonSet = new HashSet<>();
    seasonSet.add(season);
    series.setSeasons(seasonSet);

    return series;
  }

  // ✅ Helper: Convert YouTube URL to Embed format for iframe
  private String convertToEmbedUrl(String youtubeUrl) {
    if (youtubeUrl == null || youtubeUrl.isEmpty()) return "";

    // Handle youtu.be short URLs: https://youtu.be/VIDEO_ID
    if (youtubeUrl.contains("youtu.be/")) {
      String videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1).split("\\?")[0];
      return "https://www.youtube.com/embed/" + videoId;
    }

    // Handle standard watch URLs: https://www.youtube.com/watch?v=VIDEO_ID
    if (youtubeUrl.contains("youtube.com/watch?v=")) {
      String videoId = youtubeUrl.split("v=")[1].split("&")[0];
      return "https://www.youtube.com/embed/" + videoId;
    }

    // Already an embed URL or playlist
    if (youtubeUrl.contains("/embed/")) {
      return youtubeUrl;
    }

    // Fallback: return as-is
    return youtubeUrl;
  }

  // ✅ Helper: Safely get text from JsonNode with trailing space keys
  private String getNodeText(JsonNode node, String key) {
    JsonNode field = node.get(key);
    if (field != null && field.isTextual()) {
      return field.asText().trim();
    }
    return "";
  }
}

