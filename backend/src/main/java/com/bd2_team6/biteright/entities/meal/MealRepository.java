package com.bd2_team6.biteright.entities.meal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bd2_team6.biteright.entities.user.User;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByName(String name);

    @Query("SELECT m FROM Meal m WHERE m.user.username = :username AND m.name = :name")
    Optional<Meal> findByUsernameAndName(@Param("username") String username, @Param("name") String name);

    @Query("SELECT m FROM Meal m WHERE m.user.username = :username AND m.mealId = :mealId")
    Optional<Meal> findByUsernameAndMealId(@Param("username") String username, @Param("mealId") Long mealId);

    Set<Meal> findAllByUserAndMealDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
