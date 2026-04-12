package org.example.demo.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.demo.dto.series.SeriesListDTO;
import org.example.demo.entity.Series;
import org.example.demo.entity.User;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.LibraryRepo;
import org.example.demo.repository.SeriesRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibraryService {

  private final LibraryRepo libraryRepo;
  private final SeriesRepo seriesRepository;

  // ✅ Add series to user's library
  @Transactional
  @CacheEvict(value = "user:library", key = "#username")
  public SeriesListDTO addToLibrary(String username, Long seriesId) {
    User user =
        libraryRepo
            .findUserWithLibraryByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    Series series =
        seriesRepository
            .findById(seriesId)
            .orElseThrow(() -> new ResourceNotFoundException("Series", "id", seriesId));

    user.addToLibrary(series);
    libraryRepo.save(user);

    return SeriesListDTO.fromEntity(series);
  }

  // ✅ Remove series from library
  @Transactional
  @CacheEvict(value = "user:library", key = "#username")
  public void removeFromLibrary(String username, Long seriesId) {
    User user =
        libraryRepo
            .findUserWithLibraryByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    Series series =
        seriesRepository
            .findById(seriesId)
            .orElseThrow(() -> new ResourceNotFoundException("Series", "id", seriesId));

    user.removeFromLibrary(series);
    libraryRepo.save(user);
  }

  // ✅ Get user's library (cached)
  @Cacheable(value = "user:library", key = "#username")
  @Transactional(readOnly = true)
  public List<SeriesListDTO> getLibraryByUsername(String username) {
    User user =
        libraryRepo
            .findUserWithLibraryByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    return user.getLibrary().stream().map(SeriesListDTO::fromEntity).toList();
  }

  // ✅ Check if series is in library (efficient - single COUNT query)
  @Transactional(readOnly = true)
  public boolean isInLibrary(String username, Long seriesId) {
    return libraryRepo.existsByUsernameAndSeriesId(username, seriesId);
  }
}
