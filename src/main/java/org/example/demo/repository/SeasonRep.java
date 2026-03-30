package org.example.demo.repository;

import org.example.demo.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRep extends JpaRepository<Season, Long> {
}
