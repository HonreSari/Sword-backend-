package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.PageResponseDTO;
import org.example.demo.dto.series.SeriesDetailDTO;
import org.example.demo.dto.series.SeriesListDTO;
import org.example.demo.service.SeriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// src/main/java/org/example/demo/controller/SeriesController.java

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SeriesController {

  private final SeriesService seriesService;

  // ✅ UPDATED: Handle optional search query and genre parameters
  @GetMapping
  public ResponseEntity<PageResponseDTO<SeriesListDTO>> listSeries(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "12") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String genre) { // ✅ Add genre param

    PageResponseDTO<SeriesListDTO> result;

    if (q != null && !q.trim().isEmpty()) {
      // ✅ Use search endpoint if query provided
      result = seriesService.searchSeries(q.trim(), page, size);
    } else if (genre != null && !genre.trim().isEmpty()) {
      // ✅ Use genre endpoint if genre provided
      result = seriesService.getSeriesByGenre(genre.trim(), page, size);
    } else {
      // ✅ Use default endpoint if no query or genre
      result = seriesService.getAllSeries(page, size);
    }

    return ResponseEntity.ok(result);
  }

  // ✅ Existing: Get series detail
  @GetMapping("/{id}")
  public ResponseEntity<SeriesDetailDTO> getSeries(@PathVariable Long id) {
    return ResponseEntity.ok(seriesService.getSeriesById(id));
  }
}
