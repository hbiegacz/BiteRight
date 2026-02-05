package com.bd2_team6.biteright.recipe_content;

import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.recipe.Recipe;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;
import com.bd2_team6.biteright.entities.recipe.RecipeRepository;
import com.bd2_team6.biteright.entities.recipe_content.RecipeContent;
import com.bd2_team6.biteright.entities.recipe_content.RecipeContentRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RecipeContentTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeContentRepository recipeContentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveRecipeContent() {
        Recipe recipe = new Recipe("Spaghetti Bolognese", "Delicious pasta with meat sauce");
        recipeRepository.save(recipe);

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);
        ingredientRepository.save(ingredient);

        RecipeContent recipeContent = new RecipeContent(recipe, ingredient, 200);
        recipeContentRepository.save(recipeContent);

        RecipeContent foundContent = recipeContentRepository.findById(recipeContent.getRecipeContentId()).orElse(null);
        assertNotNull(foundContent);
        assertEquals(recipeContent.getIngredientAmount(), foundContent.getIngredientAmount());
        assertEquals(recipeContent.getRecipe().getName(), foundContent.getRecipe().getName());
        assertEquals(recipeContent.getIngredient().getName(), foundContent.getIngredient().getName());
    }

    @Test
    public void shouldUpdateRecipeContent() {
        Recipe recipe = new Recipe("Pasta Carbonara", "Creamy pasta with bacon");
        recipeRepository.save(recipe);

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);
        ingredientRepository.save(ingredient);

        RecipeContent recipeContent = new RecipeContent(recipe, ingredient, 150);
        recipeContentRepository.save(recipeContent);

        recipeContent.setIngredientAmount(200);
        recipeContentRepository.save(recipeContent);

        RecipeContent updatedContent = recipeContentRepository.findById(recipeContent.getRecipeContentId()).orElse(null);
        assertNotNull(updatedContent);
        assertEquals(200, updatedContent.getIngredientAmount());
    }

    @Test
    public void shouldDeleteRecipeContent() {
        Recipe recipe = new Recipe("Salad", "Fresh vegetable salad");
        recipeRepository.save(recipe);

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);
        ingredientRepository.save(ingredient);

        RecipeContent recipeContent = new RecipeContent(recipe, ingredient, 100);
        recipeContentRepository.save(recipeContent);

        recipeContentRepository.delete(recipeContent);

        RecipeContent deletedContent = recipeContentRepository.findById(recipeContent.getRecipeContentId()).orElse(null);
        assertNull(deletedContent);
    }

    @Test
    public void shouldFindRecipeContentByRecipe() {
        Recipe recipe = new Recipe("Burger", "Beef burger with cheese");
        recipeRepository.save(recipe);

        Ingredient ingredient1 = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);
        ingredientRepository.save(ingredient1);

        RecipeContent recipeContent1 = new RecipeContent(recipe, ingredient1, 150);
        RecipeContent recipeContent2 = new RecipeContent(recipe, ingredient1, 50);
        recipeContentRepository.save(recipeContent1);
        recipeContentRepository.save(recipeContent2);

        entityManager.flush();
        entityManager.clear();

        Recipe refreshedRecipe = recipeRepository.findById(recipe.getRecipeId()).orElse(null);
        assertNotNull(refreshedRecipe);

        assertEquals(2, refreshedRecipe.getRecipeContents().size());
    }

    @Test
    public void shouldFindRecipeContentByIngredient() {
        Recipe recipe = new Recipe("Burger", "Beef burger with cheese");
        recipeRepository.save(recipe);

        Ingredient ingredient1 = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);
        ingredientRepository.save(ingredient1);

        RecipeContent recipeContent1 = new RecipeContent(recipe, ingredient1, 4);
        RecipeContent recipeContent2 = new RecipeContent(recipe, ingredient1, 200);
        recipeContentRepository.save(recipeContent1);
        recipeContentRepository.save(recipeContent2);

        entityManager.flush();
        entityManager.clear();

        Ingredient refreshedIngredient = ingredientRepository.findById(ingredient1.getIngredientId()).orElse(null);
        assertNotNull(refreshedIngredient);

        assertEquals(2, refreshedIngredient.getRecipeContents().size());
    }

    @Test
    public void shouldNotSaveSameIngredientAmountTwiceForSameRecipe() {
        Recipe recipe = new Recipe("Soup", "Vegetable soup");

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);

        RecipeContent recipeContent1 = new RecipeContent(recipe, ingredient, 100);
        RecipeContent recipeContent2 = new RecipeContent(recipe, ingredient, 100);

        recipe.getRecipeContents().add(recipeContent1);
        recipe.getRecipeContents().add(recipeContent2);
        recipeRepository.save(recipe);

        Recipe saved = recipeRepository.findById(recipe.getRecipeId()).orElseThrow();
        assertEquals(1, saved.getRecipeContents().size());
    }

    @Test
    public void shouldNotSaveSameIngredientAmountTwiceForSameIngredient() {
        Recipe recipe = new Recipe("Soup", "Vegetable soup");

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);
        ingredientRepository.save(ingredient);

        RecipeContent recipeContent1 = new RecipeContent(recipe, ingredient, 100);
        RecipeContent recipeContent2 = new RecipeContent(recipe, ingredient, 100);

        ingredient.getRecipeContents().add(recipeContent1);
        ingredient.getRecipeContents().add(recipeContent2);
        ingredientRepository.save(ingredient);

        Ingredient saved = ingredientRepository.findById(ingredient.getIngredientId()).orElseThrow();
        assertEquals(1, saved.getRecipeContents().size());
    }

    @Test
    public void shouldDeleteRecipeContentWhenRecipeDeleted() {
        Recipe recipe = new Recipe("Fried Rice", "Stir-fried rice with vegetables");

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);

        RecipeContent recipeContent1 = new RecipeContent(recipe, ingredient, 100);
        RecipeContent recipeContent2 = new RecipeContent(recipe, ingredient, 10);

        recipe.getRecipeContents().add(recipeContent1);
        recipe.getRecipeContents().add(recipeContent2);
        recipeRepository.save(recipe);

        Long recipeId = recipe.getRecipeId();
        Long recipeContent1Id = recipeContent1.getRecipeContentId();
        Long recipeContent2Id = recipeContent2.getRecipeContentId();

        recipeRepository.deleteById(recipeId);

        assertFalse(recipeRepository.findById(recipeId).isPresent());
        assertFalse(recipeContentRepository.findById(recipeContent1Id).isPresent());
        assertFalse(recipeContentRepository.findById(recipeContent2Id).isPresent());
    }

    @Test
    public void shouldDeleteRecipeContentWhenIngredientDeleted() {
        Recipe recipe = new Recipe("Pizza", "Cheese pizza");

        Ingredient ingredient = new Ingredient("Pasta", "Gerber", 100, 350,
                10, 1, 75);

        RecipeContent recipeContent1 = new RecipeContent(recipe, ingredient, 100);
        RecipeContent recipeContent2 = new RecipeContent(recipe, ingredient, 10);

        ingredient.getRecipeContents().add(recipeContent1);
        ingredient.getRecipeContents().add(recipeContent2);
        ingredientRepository.save(ingredient);

        Long ingredientId = ingredient.getIngredientId();
        Long recipeContent1Id = recipeContent1.getRecipeContentId();
        Long recipeContent2Id = recipeContent2.getRecipeContentId();

        ingredientRepository.deleteById(ingredientId);

        assertFalse(ingredientRepository.findById(ingredientId).isPresent());
        assertFalse(recipeContentRepository.findById(recipeContent1Id).isPresent());
        assertFalse(recipeContentRepository.findById(recipeContent2Id).isPresent());
    }
}
