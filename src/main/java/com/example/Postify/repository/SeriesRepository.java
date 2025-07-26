package com.example.Postify.repository;

import com.example.Postify.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface    SeriesRepository extends JpaRepository<Series, Long> {
}