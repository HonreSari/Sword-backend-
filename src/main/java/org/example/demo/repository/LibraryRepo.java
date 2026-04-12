package org.example.demo.repository;

import org.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepo extends JpaRepository<User, Long> {

  @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM User u JOIN u.library s WHERE u.id = :userId AND s.id = :seriesId")
  boolean existsByUserIdAndSeriesId(@Param("userId") Long userId, @Param("seriesId") Long seriesId);
  
  @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM User u JOIN u.library s WHERE u.username = :username AND s.id = :seriesId")
  boolean existsByUsernameAndSeriesId(@Param("username") String username, @Param("seriesId") Long seriesId);

  @Query("SELECT s.id FROM User u JOIN u.library s WHERE u.username = :username")
  List<Long> findLibrarySeriesIdsByUsername(@Param("username") String username);

  @Query("SELECT s FROM User u JOIN u.library s WHERE u.username = :username")
  List<org.example.demo.entity.Series> findLibrarySeriesByUsername(@Param("username") String username);
  
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.library WHERE u.username = :username")
  Optional<User> findUserWithLibraryByUsername(@Param("username") String username);
}
