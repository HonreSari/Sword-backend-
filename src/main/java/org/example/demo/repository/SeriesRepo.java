package org.example.demo.repository;

import java.util.Optional;
import org.example.demo.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepo extends JpaRepository<Series, Long> {

  // ✅ Existing query for detail page
  @Query(
      "SELECT DISTINCT s FROM Series s "
          + "LEFT JOIN FETCH s.seasons se "
          + "LEFT JOIN FETCH se.episodes ep "
          + "WHERE s.id = :id "
          + "ORDER BY se.seasonOrder ASC, ep.episodeNumber ASC")
  Optional<Series> findByIdWithSeasonsAndEpisodes(@Param("id") Long id);

  // ✅ NEW: Search by title or chineseTitle (case-insensitive)
  @Query(
      "SELECT s FROM Series s WHERE "
          + "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR "
          + "LOWER(s.chineseTitle) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<Series> findByTitleOrChineseTitleContaining(
      @Param("query") String query, PageRequest pageRequest);

  // ✅ NEW: Filter by genre
  @Query("SELECT s FROM Series s JOIN s.genres g WHERE LOWER(g) = LOWER(:genre)")
  Page<Series> findByGenre(@Param("genre") String genre, PageRequest pageRequest);
}
