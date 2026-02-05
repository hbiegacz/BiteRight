package com.bd2_team6.biteright.ingredient;

import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IngredientTests {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", "FreshFarm", 100, 22, 1, 0, 5);
        ingredientRepository.save(ingredient);

        Ingredient foundIngredient = ingredientRepository.findById(ingredient.getIngredientId()).orElse(null);
        assertNotNull(foundIngredient);
        assertEquals(ingredient.getName(), foundIngredient.getName());
        assertEquals(ingredient.getBrand(), foundIngredient.getBrand());
        assertEquals(ingredient.getPortionSize(), foundIngredient.getPortionSize());
        assertEquals(ingredient.getCalories(), foundIngredient.getCalories());
        assertEquals(ingredient.getProtein(), foundIngredient.getProtein());
        assertEquals(ingredient.getFat(), foundIngredient.getFat());
        assertEquals(ingredient.getCarbs(), foundIngredient.getCarbs());
    }

    @Test
    public void shouldUpdateIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", "FreshFarm", 100, 22, 1, 0, 5);
        ingredientRepository.save(ingredient);

        ingredient.setCalories(30);
        ingredientRepository.save(ingredient);

        Ingredient updatedIngredient = ingredientRepository.findById(ingredient.getIngredientId()).orElse(null);
        assertNotNull(updatedIngredient);
        assertEquals(30, updatedIngredient.getCalories());
    }

    @Test
    public void shouldDeleteIngredient() {
        Ingredient ingredient = new Ingredient("Tomato", "FreshFarm", 100, 22, 1, 0, 5);
        ingredientRepository.save(ingredient);

        ingredientRepository.delete(ingredient);

        Ingredient deletedIngredient = ingredientRepository.findById(ingredient.getIngredientId()).orElse(null);
        assertNull(deletedIngredient);
    }
}
