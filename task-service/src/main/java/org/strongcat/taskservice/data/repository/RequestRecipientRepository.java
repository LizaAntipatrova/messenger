package org.strongcat.taskservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.taskservice.data.entity.RequestRecipient;

@Repository
public interface RequestRecipientRepository extends JpaRepository<RequestRecipient, Long> {
}
