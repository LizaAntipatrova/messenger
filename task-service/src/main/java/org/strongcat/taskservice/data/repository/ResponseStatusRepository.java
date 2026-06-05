package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.enums.ResponseStatusName;
import org.strongcat.taskservice.data.entity.ResponseStatus;

import java.util.Optional;

@Repository
public interface ResponseStatusRepository extends JpaRepository<ResponseStatus, Long> {

    Optional<ResponseStatus> findByName(String name);

    default Optional<ResponseStatus> findByName(ResponseStatusName statusName) {
        return findByName(statusName.getDatabaseName());
    }
}
