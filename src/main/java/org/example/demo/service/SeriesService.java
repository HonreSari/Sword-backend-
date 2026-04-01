package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.Dto.PageResponseDTO;
import org.example.demo.Dto.series.SeriesDetailDTO;
import org.example.demo.Dto.series.SeriesListDTO;
import org.example.demo.entity.Series;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.SeriesRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeriesService {

  private final SeriesRepo seriesRepository;

  // ✅ CACHE PageResponseDTO (not Page<SeriesListDTO>)
  @Cacheable(value = "series:list", key = "#page + ':' + #size")
  public PageResponseDTO<SeriesListDTO> getAllSeries(int page, int size) {
    Page<Series> entities = seriesRepository.findAll(
        PageRequest.of(page, size, Sort.by("createdAt").descending()));

    // ✅ Convert Page → PageResponseDTO BEFORE caching
    return PageResponseDTO.from(entities.map(SeriesListDTO::fromEntity));
  }

  @Cacheable(value = "series:detail", key = "#id")
  public SeriesDetailDTO getSeriesById(Long id) {
    Series series = seriesRepository.findByIdWithSeasonsAndEpisodes(id)
        .orElseThrow(() -> new ResourceNotFoundException("Series", "id", id));
    return SeriesDetailDTO.fromEntity(series);
  }
}
