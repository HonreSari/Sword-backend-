// src/main/java/org/example/demo/controller/EpisodeController.java

package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.EpisodeStreamDTO;
import org.example.demo.service.EpisodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/episodes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // ✅ Restrict to frontend origin
public class EpisodeController {

  private final EpisodeService episodeService;

  // ✅ Option A: Keep /stream suffix (recommended for clarity)
  @GetMapping("/{id}/stream")
  public ResponseEntity<EpisodeStreamDTO> getStreamInfo(@PathVariable Long id) {
    try {
      EpisodeStreamDTO dto = episodeService.getEpisodeForStreaming(id);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  // ✅ Option B: Also support /{id} for simplicity (optional)
  @GetMapping("/{id}")
  public ResponseEntity<EpisodeStreamDTO> getEpisode(@PathVariable Long id) {
    return getStreamInfo(id); // Reuse the same logic
  }
}
