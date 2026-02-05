package com.bd2_team6.biteright.entities.meal_type;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MealTypeRepository extends JpaRepository<MealType, Long> {
    Optional<MealType> findByName(String name);
}
