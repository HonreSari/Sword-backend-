package org.example.demo.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
      } catch (Exception e) {
        System.err.println("❌ Failed to seed: " + title);
        e.printStackTrace();
      }
    }
    System.out.println("🎉 Successfully seeded " + count + " donghua!");
  }

  private Series convertToEntity(JsonNode node) {
    Series series = new Series();

    series.setTitle(getNodeText(node, "A "));
    series.setChineseTitle(getNodeText(node, "B "));
    series.setCoverImageUrl(getNodeText(node, "C "));

    series.setDescription(getNodeText(node, "I "));

    // Parse Rating
    try {
      series.setRating(Double.parseDouble(getNodeText(node, "E ")));
    } catch (NumberFormatException e) {
      series.setRating(0.0);
    }

    series.setStatus(getNodeText(node, "F "));

    // Parse Total Episodes
    int totalEpisodes = 0;
    try {
      totalEpisodes = Integer.parseInt(getNodeText(node, "G "));
    } catch (NumberFormatException e) {
      totalEpisodes = 0;
    }
    series.setTotalEpisodes(totalEpisodes);

    // Parse Genres
    String genresStr = getNodeText(node, "H ");
    List<String> genres =
        Arrays.stream(genresStr.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    series.setGenres(genres);

    // --- 🔄 GENERATE EPISODES ---

    // 1. Build Playlist URL from Column D
    String rawUrl = getNodeText(node, "D ");
    String videoUrl = "";
    if (!rawUrl.isEmpty()) {
      if (rawUrl.contains("youtu.be/")) {
        // Convert short URL to embed format
        String videoId = rawUrl.substring(rawUrl.lastIndexOf("/") + 1).split("\\?")[0];
        videoUrl = "https://www.youtube.com/embed/" + videoId;
      } else if (rawUrl.contains("youtube.com/watch?v=")) {
        String videoId = rawUrl.split("v=")[1].split("&")[0];
        videoUrl = "https://www.youtube.com/embed/" + videoId;
      } else if (rawUrl.contains("/embed/")) {
        videoUrl = rawUrl; // Already embed format
      } else {
        // Assume it's a playlist ID
        videoUrl = "https://www.youtube.com/embed/videoseries?list=" + rawUrl;
      }
    } // 2. Create Season 1
    Season season = new Season();
    season.setSeasonName("Season 1");
    season.setSeasonOrder(1);
    season.setSeries(series);

    // 3. Create multiple episodes (Loop up to 10 for demo performance)
    int episodesToCreate = Math.min(totalEpisodes, 10); // Cap at 10 episodes
    if (episodesToCreate == 0) episodesToCreate = 1;

    Set<Episode> episodes = new HashSet<>();
    for (int i = 1; i <= episodesToCreate; i++) {
      Episode ep = new Episode();
      ep.setTitle("Episode " + i);
      ep.setEpisodeNumber(i);
      ep.setDuration("24m");
      // Point all episodes to the Playlist URL (User can switch videos inside the player)
      ep.setVideoUrl(videoUrl);
      ep.setSeason(season);
      episodes.add(ep);
    }

    season.setEpisodes(episodes);
    series.setSeasons(new HashSet<>(List.of(season)));

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
