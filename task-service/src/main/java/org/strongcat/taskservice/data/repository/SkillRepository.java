package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
}
