package com.bd2_team6.biteright.entities.recipe_content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeContentRepository extends JpaRepository<RecipeContent, Long> {
    
}
