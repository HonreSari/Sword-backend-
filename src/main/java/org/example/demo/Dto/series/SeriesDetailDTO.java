package org.example.demo.Dto.series;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.example.demo.entity.Season;
import org.example.demo.entity.Series;
import org.example.demo.Dto.EpisodeListItemDTO;

public record SeriesDetailDTO(
    Long id,
    String title,
    String chineseTitle,
    String description,
    String coverImageUrl,
    Double rating,
    List<String> genres,
    List<SeasonDTO> seasons) implements Serializable {

  private static final long serialVersionUID = 1L;

  public record SeasonDTO(
      Integer seasonOrder,
      String seasonName,
      List<EpisodeListItemDTO> episodes) implements Serializable {
    private static final long serialVersionUID = 1L;
    public static SeasonDTO fromEntity(Season season) {
      return new SeasonDTO(
          season.getSeasonOrder(),
          season.getSeasonName(),
          Optional.ofNullable(season.getEpisodes())
              .orElse(List.of())
              .stream()
              .map(EpisodeListItemDTO::fromEntity)
              .toList());
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
        series.getGenres(),
        Optional.ofNullable(series.getSeasons())
            .orElse(List.of())
            .stream()
            .map(SeasonDTO::fromEntity)
            .toList());
  }
}
