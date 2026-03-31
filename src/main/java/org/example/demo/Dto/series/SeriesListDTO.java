package org.example.demo.Dto.series;

import java.util.List;
import org.example.demo.entity.Series;

public record SeriesListDTO(
    Long id,
    String title,
    String chineseTitle,
    String coverImageUrl,
    Double rating,
    String status,
    Integer totalEpisodes,
    List<String> genres
) {
    
    /**
     * Convert Series entity → SeriesListDTO
     * Lightweight version for grid/home page display
     */
    public static SeriesListDTO fromEntity(Series series) {
        return new SeriesListDTO(
            series.getId(),
            series.getTitle(),
            series.getChineseTitle(),
            series.getCoverImageUrl(),
            series.getRating(),
            series.getStatus(),
            series.getTotalEpisodes(),
            series.getGenres()
        );
    }
}
