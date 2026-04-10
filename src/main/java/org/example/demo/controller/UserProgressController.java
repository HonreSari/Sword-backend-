package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.progress.UserProgressRequest;
import org.example.demo.dto.progress.UserProgressResponse;
import org.example.demo.service.UserProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProgressController {

  private final UserProgressService progressService;

  // ✅ Save progress (protected)
  @PostMapping
  public ResponseEntity<UserProgressResponse> saveProgress(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody UserProgressRequest request) {

    String username = userDetails.getUsername();
    return ResponseEntity.ok(progressService.saveProgress(username, request));
  }

  // ✅ Get progress (protected + cached)
  @GetMapping
  public ResponseEntity<List<UserProgressResponse>> getProgress(
      @AuthenticationPrincipal UserDetails userDetails) {

    String username = userDetails.getUsername();
    return ResponseEntity.ok(progressService.getProgressByUsername(username));
  }
}
