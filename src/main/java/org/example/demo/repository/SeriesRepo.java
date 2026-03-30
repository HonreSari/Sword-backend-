package org.example.demo.repository;

import org.example.demo.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepo extends JpaRepository<Series, Long> {
}
