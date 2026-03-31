package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Dto.PageResponseDTO;
import org.example.demo.Dto.series.SeriesDetailDTO;
import org.example.demo.Dto.series.SeriesListDTO;
import org.example.demo.service.SeriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow Frontend (localhost:5173) later
public class SeriesController {

  private final SeriesService seriesService;

  @GetMapping
  public ResponseEntity<PageResponseDTO<SeriesListDTO>> listSeries(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "12") int size) {

    var result = seriesService.getAllSeries(page, size);
    return ResponseEntity.ok(PageResponseDTO.from(result));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SeriesDetailDTO> getSeries(@PathVariable Long id) {
    return ResponseEntity.ok(seriesService.getSeriesById(id));
  }
}
