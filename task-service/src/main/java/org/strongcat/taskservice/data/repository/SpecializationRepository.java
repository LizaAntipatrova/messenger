package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.Specialization;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
}
