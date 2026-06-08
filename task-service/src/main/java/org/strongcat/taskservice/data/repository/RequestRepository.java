package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.Request;

import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r " +
            "JOIN RequestRecipient rr ON rr.request.id = r.id " +
            "WHERE r.id = :requestId " +
            "AND rr.specialist.externalUserId = :specialistExternalUserId " +
            "AND rr.responseStatus.name = 'ACCEPTED'")
    Optional<Request> findAcceptedRequestBySpecialist(
            @Param("requestId") Long requestId,
            @Param("specialistExternalUserId") Long specialistExternalUserId
    );


}
