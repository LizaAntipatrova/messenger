package org.strongcat.vitrinaservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.SkillDimension;

import java.util.Optional;

@Repository
public interface SkillDimensionRepository extends JpaRepository<SkillDimension, Long> {
    Optional<SkillDimension> findBySkillName(String skillName);

    @Modifying
    @Query(value = """
            insert into d_skill (skill_key, skill_name)
            values (:skillKey, :skillName)
            on conflict (skill_key) do update
            set skill_name = excluded.skill_name
            """, nativeQuery = true)
    void upsertSkill(@Param("skillKey") Long skillKey, @Param("skillName") String skillName);
}
