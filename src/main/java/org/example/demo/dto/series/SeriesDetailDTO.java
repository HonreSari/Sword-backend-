package org.example.demo.dto.series;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.demo.entity.Season;
import org.example.demo.entity.Series;
import org.example.demo.dto.EpisodeListItemDTO;

public record SeriesDetailDTO(
    Long id,
    String title,
    String chineseTitle,
    String description,
    String coverImageUrl,
    Double rating,
    List<String> genres,
    List<SeasonDTO> seasons) {

  public record SeasonDTO(
      Integer seasonOrder,
      String seasonName,
      List<EpisodeListItemDTO> episodes) {
    public static SeasonDTO fromEntity(Season season) {
      return new SeasonDTO(
          season.getSeasonOrder(),
          season.getSeasonName(),
          new ArrayList<>(
              Optional.ofNullable(season.getEpisodes())
                  .orElse(Set.of())
                  .stream()
                  .map(EpisodeListItemDTO::fromEntity)
                  .toList()));
    }
  }

  public static SeriesDetailDTO fromEntity(Series series) {
    return new SeriesDetailDTO(
        series.getId(),
        series.getTitle(),
        series.getChineseTitle(),
        series.getDescription(),
        series.getCoverImageUrl(),
        series.getRating(),
        new ArrayList<>(series.getGenres()),
        new ArrayList<>(
            Optional.ofNullable(series.getSeasons())
                .orElse(Set.of())
                .stream()
                .map(SeasonDTO::fromEntity)
                .toList()));
  }
}
