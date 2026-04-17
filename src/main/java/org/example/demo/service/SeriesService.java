package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.PageResponseDTO;
import org.example.demo.dto.series.SeriesDetailDTO;
import org.example.demo.dto.series.SeriesListDTO;
import org.example.demo.entity.Series;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.SeriesRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ Add this import

@Service
@RequiredArgsConstructor
public class SeriesService {

  private final SeriesRepo seriesRepository;

  // ✅ Existing: Get all series (paginated + cached)
  @Transactional(readOnly = true)
  @Cacheable(value = "series:list", key = "#page + ':' + #size")
  public PageResponseDTO<SeriesListDTO> getAllSeries(int page, int size) {
    Page<Series> entities =
        seriesRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    return PageResponseDTO.from(entities.map(SeriesListDTO::fromEntity));
  }

  // ✅ NEW: Search series (paginated + cached with query key)
  @Transactional(readOnly = true)
  @Cacheable(value = "series:search", key = "#query + ':' + #page + ':' + #size")
  public PageResponseDTO<SeriesListDTO> searchSeries(String query, int page, int size) {
    Page<Series> results =
        seriesRepository.findByTitleOrChineseTitleContaining(query, PageRequest.of(page, size));
    return PageResponseDTO.from(results.map(SeriesListDTO::fromEntity));
  }

  // ✅ Existing: Get series detail (cached)
  @Cacheable(value = "series:detail", key = "#id")
  public SeriesDetailDTO getSeriesById(Long id) {
    Series series =
        seriesRepository
            .findByIdWithSeasonsAndEpisodes(id)
            .orElseThrow(() -> new ResourceNotFoundException("Series", "id", id));
    return SeriesDetailDTO.fromEntity(series);
  }
}
