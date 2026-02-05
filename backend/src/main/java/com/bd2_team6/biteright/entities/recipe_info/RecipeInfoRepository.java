package com.bd2_team6.biteright.entities.recipe_info;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeInfoRepository extends JpaRepository<RecipeInfo, Long> {
    Optional<RecipeInfo> findByRecipeName(String recipeName);
}
