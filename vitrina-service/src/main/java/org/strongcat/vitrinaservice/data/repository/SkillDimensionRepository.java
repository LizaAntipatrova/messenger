package org.strongcat.vitrinaservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.SkillDimension;

import java.util.Optional;

@Repository
public interface SkillDimensionRepository extends JpaRepository<SkillDimension, Long> {
    Optional<SkillDimension> findBySkillName(String skillName);
}