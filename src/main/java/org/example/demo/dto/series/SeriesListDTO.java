package org.example.demo.Dto.series;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList; // ✅ Must import this
import java.util.List;
import org.example.demo.entity.Series;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record SeriesListDTO(
    Long id,
    String title,
    String chineseTitle,
    String coverImageUrl,
    Double rating,
    String status,
    Integer totalEpisodes,
    List<String> genres) {
  public static SeriesListDTO fromEntity(Series series) {
    return new SeriesListDTO(
        series.getId(),
        series.getTitle(),
        series.getChineseTitle(),
        series.getCoverImageUrl(),
        series.getRating(),
        series.getStatus(),
        series.getTotalEpisodes(),
        // ✅ CRITICAL: Copy to new ArrayList (breaks Hibernate proxy)
        new ArrayList<>(series.getGenres()));
  }
}
