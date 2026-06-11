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

    @Modifying
    @Query(value = """
            update d_user
            set prefered_complexity = (
                select coalesce(avg(t.experience), 0.0)::real
                from fact_user f
                join d_task t on f.task_key = t.task_key
                where f.user_key = :userKey
            )
            where user_key = :userKey
            """, nativeQuery = true)
    void updatePreferedComplexity(@Param("userKey") Long userKey);

    @Modifying
    @Query(value = """
            update d_user
            set top_task_category = (
                with ranked_categories as (
                    select t.category_id, count(*) as cnt,
                           row_number() over (order by count(*) desc) as rn
                    from fact_user f
                    join d_task t on f.task_key = t.task_key
                    where f.user_key = :userKey and t.category_id is not null
                    group by t.category_id
                )
                select array_agg(category_id order by rn)
                from ranked_categories
                where rn <= 3
            )
            where user_key = :userKey
            """, nativeQuery = true)
    void updateTopTaskCategories(@Param("userKey") Long userKey);

}
