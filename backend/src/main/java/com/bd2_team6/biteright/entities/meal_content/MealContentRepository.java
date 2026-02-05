package com.bd2_team6.biteright.entities.meal_content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MealContentRepository extends JpaRepository<MealContent, Long> {
    
}
