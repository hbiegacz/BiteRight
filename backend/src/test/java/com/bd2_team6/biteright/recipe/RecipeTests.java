package com.bd2_team6.biteright.recipe;

import com.bd2_team6.biteright.entities.recipe.Recipe;
import com.bd2_team6.biteright.entities.recipe.RecipeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RecipeTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveRecipe() {
        Recipe recipe = new Recipe("Pasta", "Delicious pasta with tomato sauce");
        recipeRepository.save(recipe);

        Recipe foundRecipe = recipeRepository.findById(recipe.getRecipeId()).orElse(null);
        assertNotNull(foundRecipe);
        assertEquals(recipe.getName(), foundRecipe.getName());
        assertEquals(recipe.getDescription(), foundRecipe.getDescription());
    }

    @Test
    public void shouldUpdateRecipe() {
        Recipe recipe = new Recipe("Pasta", "Delicious pasta with tomato sauce");
        recipeRepository.save(recipe);

        recipe.setName("Spaghetti");
        recipe.setDescription("Spaghetti with meatballs");
        recipeRepository.save(recipe);

        Recipe updatedRecipe = recipeRepository.findById(recipe.getRecipeId()).orElse(null);
        assertNotNull(updatedRecipe);
        assertEquals("Spaghetti", updatedRecipe.getName());
        assertEquals("Spaghetti with meatballs", updatedRecipe.getDescription());
    }

    @Test
    public void shouldDeleteRecipe() {
        Recipe recipe = new Recipe("Pasta", "Delicious pasta with tomato sauce");
        recipeRepository.save(recipe);

        recipeRepository.delete(recipe);

        Recipe deletedRecipe = recipeRepository.findById(recipe.getRecipeId()).orElse(null);
        assertNull(deletedRecipe);
    }
}
