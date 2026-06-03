package org.strongcat.vitrinaservice.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.strongcat.vitrinaservice.data.entity.UserDimension;

import java.util.Optional;

@Repository
public interface UserDimensionRepository extends JpaRepository<UserDimension, Long> {

    // Ищет последнюю добавленную версию пользователя
    Optional<UserDimension> findFirstByIdNaturalUserOrderByUserKeyDesc(Integer idNaturalUser);
}