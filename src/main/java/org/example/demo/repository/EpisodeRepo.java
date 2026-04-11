package org.example.demo.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.example.demo.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeRepo extends JpaRepository<Episode, Long> {
  @Query("SELECT e FROM Episode e JOIN FETCH e.season s JOIN FETCH s.series sr WHERE e.id = :id")
  Optional<Episode> findByIdWithDetails(@Param("id") Long id);

  // ✅ For next/prev navigation
  @Query(
      "SELECT e FROM Episode e WHERE e.season.id = :seasonId AND e.episodeNumber > :episodeNumber"
          + " ORDER BY e.episodeNumber ASC")
  List<Episode> findBySeasonIdAndEpisodeNumberGreaterThan(
      @Param("seasonId") Long seasonId, @Param("episodeNumber") Integer episodeNumber);

  @Query(
      "SELECT e FROM Episode e WHERE e.season.id = :seasonId AND e.episodeNumber < :episodeNumber"
          + " ORDER BY e.episodeNumber DESC")
  List<Episode> findBySeasonIdAndEpisodeNumberLessThan(
      @Param("seasonId") Long seasonId, @Param("episodeNumber") Integer episodeNumber);
}
