package org.example.demo.repository;

import java.util.Optional;

import org.example.demo.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepo extends JpaRepository<Series, Long> {
  @Query("SELECT DISTINCT s FROM Series s " +
      "LEFT JOIN FETCH s.seasons se " +
      "LEFT JOIN FETCH se.episodes " +
      "WHERE s.id = :id")
  Optional<Series> findByIdWithSeasonsAndEpisodes(@Param("id") Long id);
}
