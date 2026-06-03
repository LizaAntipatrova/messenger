package org.strongcat.vitrinaservice.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.UserFact;

@Repository
public interface UserFactRepository extends JpaRepository<UserFact, Long> {

}