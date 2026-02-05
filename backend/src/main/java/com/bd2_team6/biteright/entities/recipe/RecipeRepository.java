package com.bd2_team6.biteright.entities.recipe;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Set<Recipe> findByNameContainingIgnoreCase(String name);
    Optional<Recipe> findByName(String name);
}
