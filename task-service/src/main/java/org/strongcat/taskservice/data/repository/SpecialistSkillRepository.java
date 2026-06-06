package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.SpecialistSkill;

import java.util.List;

@Repository
public interface SpecialistSkillRepository extends JpaRepository<SpecialistSkill, Long> {

    @Query("SELECT ss FROM SpecialistSkill ss " +
            "WHERE ss.specialist.id IN :specialistIds")
    List<SpecialistSkill> findAllBySpecialistIds(@Param("specialistIds") List<Long> specialistIds);

    void deleteAllBySpecialistId(Long specialistId);
}
