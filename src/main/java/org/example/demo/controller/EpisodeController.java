package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Dto.EpisodeStreamDTO;
import org.example.demo.service.EpisodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/episodes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EpisodeController {

  private final EpisodeService episodeService;

  @GetMapping("/{id}/stream")
  public ResponseEntity<EpisodeStreamDTO> getStreamInfo(@PathVariable Long id) {
    return ResponseEntity.ok(episodeService.getEpisodeForStreaming(id));
  }
}
