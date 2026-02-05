package com.bd2_team6.biteright.entities.meal_info;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MealInfoRepository extends JpaRepository<MealInfo, Long> {
    Optional<MealInfo> findByMealName(String mealName);
}
