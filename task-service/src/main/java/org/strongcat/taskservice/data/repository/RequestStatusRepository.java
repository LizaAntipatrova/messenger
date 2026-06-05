package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.enums.RequestStatusName;
import org.strongcat.taskservice.data.entity.RequestStatus;

import java.util.Optional;

@Repository
public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {

    Optional<RequestStatus> findByName(String name);

    default Optional<RequestStatus> findByName(RequestStatusName statusName) {
        return findByName(statusName.getDatabaseName());
    }
}
