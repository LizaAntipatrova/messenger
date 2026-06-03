package org.strongcat.vitrinaservice.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.strongcat.vitrinaservice.data.entity.UserAnalyticsView;

import java.util.Optional;

@Repository
public interface UserAnalyticsViewRepository extends JpaRepository<UserAnalyticsView, Integer> {

    Optional<UserAnalyticsView> findByIdNaturalUser(Integer idNaturalUser);

    @Modifying
    @Transactional
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_analytics", nativeQuery = true)
    void refreshAnalyticsView();
}