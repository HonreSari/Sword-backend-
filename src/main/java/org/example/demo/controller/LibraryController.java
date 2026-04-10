package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.series.SeriesListDTO;
import org.example.demo.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibraryController {

  private final LibraryService libraryService;

  // ✅ Add to library
  @PostMapping("/{seriesId}")
  public ResponseEntity<SeriesListDTO> addToLibrary(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long seriesId) {

    String username = userDetails.getUsername();
    return ResponseEntity.ok(libraryService.addToLibrary(username, seriesId));
  }

  // ✅ Remove from library
  @DeleteMapping("/{seriesId}")
  public ResponseEntity<Void> removeFromLibrary(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long seriesId) {

    String username = userDetails.getUsername();
    libraryService.removeFromLibrary(username, seriesId);
    return ResponseEntity.noContent().build();
  }

  // ✅ Get library
  @GetMapping
  public ResponseEntity<List<SeriesListDTO>> getLibrary(
      @AuthenticationPrincipal UserDetails userDetails) {

    System.out.println("🔍 [DEBUG] Controller hit! userDetails = " + userDetails);

    if (userDetails == null) {
      System.out.println("❌ [DEBUG] userDetails is null");
      return ResponseEntity.status(401).build();
    }

    String username = userDetails.getUsername();
    System.out.println("🔍 [DEBUG] Username = " + username);

    try {
      System.out.println("🔍 [DEBUG] Calling service...");
      List<SeriesListDTO> library = libraryService.getLibraryByUsername(username);
      System.out.println("✅ [DEBUG] Service returned " + library.size() + " items");
      return ResponseEntity.ok(library);
    } catch (Exception e) {
      System.err.println("❌ [DEBUG] EXCEPTION: " + e.getClass().getName());
      System.err.println("❌ [DEBUG] Message: " + e.getMessage());
      e.printStackTrace(); // ✅ This prints the full stack trace!
      return ResponseEntity.status(500).build();
    }
  }

  // ✅ Check if in library
  @GetMapping("/{seriesId}/check")
  public ResponseEntity<Boolean> isInLibrary(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long seriesId) {

    String username = userDetails.getUsername();
    return ResponseEntity.ok(libraryService.isInLibrary(username, seriesId));
  }
}
