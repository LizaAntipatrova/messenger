package org.strongcat.vitrinaservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.DateDimension;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DateDimensionRepository extends JpaRepository<DateDimension, Long> {
    Optional<DateDimension> findByFullDate(LocalDate fullDate);
}