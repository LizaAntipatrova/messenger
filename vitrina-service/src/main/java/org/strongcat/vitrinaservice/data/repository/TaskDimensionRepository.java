package org.strongcat.vitrinaservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.TaskDimension;

import java.util.Optional;

@Repository
public interface TaskDimensionRepository extends JpaRepository<TaskDimension, Long> {
    Optional<TaskDimension> findByIdNaturalTask(Integer idNaturalTask);
}