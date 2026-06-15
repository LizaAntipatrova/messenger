package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.RequestRecipient;
import org.strongcat.taskservice.data.entity.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRecipientRepository extends JpaRepository<RequestRecipient, Long> {

    @Query("SELECT rr FROM RequestRecipient rr " +
            "WHERE rr.request.id = :requestId")
//            "AND rr.specialist.externalUserId = :externalUserId")
    List<RequestRecipient> findByRequestIdAndSpecialistExternalUserId(
            @Param("requestId") Long requestId,
            @Param("externalUserId") Long externalUserId
    );

//    Optional<RequestStatus> findByName(String name);


    @Query("SELECT rr FROM RequestRecipient rr " +
            "JOIN FETCH rr.specialist s " +
            "WHERE rr.request.id = :requestId " +
            "AND rr.responseStatus.name = 'ACCEPTED'")
    Optional<RequestRecipient> findAcceptedRecipientByRequestId(@Param("requestId") Long requestId);
}
