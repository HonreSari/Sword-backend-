package org.example.demo.repository;

import org.example.demo.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibrayRepo extends JpaRepository<Library, Long> {
  boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);
}
