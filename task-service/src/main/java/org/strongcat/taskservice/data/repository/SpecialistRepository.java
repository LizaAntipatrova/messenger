package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.Specialist;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {

    @Query("SELECT s FROM Specialist s " +
            "WHERE s.specialization.id = :specId " +
            "AND s.minPayment <= :maxPayment " +
            "AND (:reqExperience IS NULL OR s.experienceMonths >= :reqExperience)")
    List<Specialist> findPotentialCandidates(
            @Param("specId") Long specId,
            @Param("maxPayment") BigDecimal maxPayment,
            @Param("reqExperience") Integer reqExperience
    );


    Optional<Specialist> findByExternalUserId(Long externalId);
}
