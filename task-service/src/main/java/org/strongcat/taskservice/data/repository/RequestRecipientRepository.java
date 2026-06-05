package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.RequestRecipient;

import java.util.Optional;

@Repository
public interface RequestRecipientRepository extends JpaRepository<RequestRecipient, Long> {

    @Query("SELECT rr FROM RequestRecipient rr " +
            "WHERE rr.request.id = :requestId " +
            "AND rr.specialist.externalUserId = :externalUserId")
    Optional<RequestRecipient> findByRequestIdAndSpecialistExternalUserId(
            @Param("requestId") Long requestId,
            @Param("externalUserId") Long externalUserId
    );
}
