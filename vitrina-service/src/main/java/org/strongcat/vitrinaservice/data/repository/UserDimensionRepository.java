package org.strongcat.vitrinaservice.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.UserDimension;

import java.util.Optional;

@Repository
public interface UserDimensionRepository extends JpaRepository<UserDimension, Long> {

    Optional<UserDimension> findFirstByIdNaturalUserOrderByUserKeyDesc(Integer idNaturalUser);

    @Modifying
    @Query(value = """
            insert into link_user_skill (user_key, skill_key)
            select :userKey, :skillKey
            where exists (select 1 from d_skill where skill_key = :skillKey)
            on conflict do nothing
            """, nativeQuery = true)
    int insertUserSkillLink(@Param("userKey") Long userKey, @Param("skillKey") Long skillKey);

    @Modifying
    @Query(value = """
            delete from link_user_skill
            where user_key = :userKey and skill_key = :skillKey
            """, nativeQuery = true)
    int deleteUserSkillLink(@Param("userKey") Long userKey, @Param("skillKey") Long skillKey);
}
