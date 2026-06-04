package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.RequestSkill;

@Repository
public interface RequestSkillRepository extends JpaRepository<RequestSkill, Long> {
}
