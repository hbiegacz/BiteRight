package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.DTO.RecipeContentDTO;
import com.bd2_team6.biteright.controllers.DTO.RecipeDTO;
import com.bd2_team6.biteright.controllers.DTO.RecipeMacrosDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.RecipeCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.RecipeUpdateRequest;
import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;
import com.bd2_team6.biteright.entities.recipe.Recipe;
import com.bd2_team6.biteright.entities.recipe.RecipeRepository;
import com.bd2_team6.biteright.entities.recipe_content.RecipeContent;

import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeMacrosDTO calculateMacros(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

        for (RecipeContent content : recipe.getRecipeContents()) {
            Ingredient ingredient = content.getIngredient();
            double amount = content.getIngredientAmount();
            double portion = ingredient.getPortionSize();

            if (portion > 0) {
                totalCalories += (amount / portion) * (ingredient.getCalories() != null ? ingredient.getCalories() : 0);
                totalProtein += (amount / portion) * (ingredient.getProtein() != null ? ingredient.getProtein() : 0);
                totalFat += (amount / portion) * (ingredient.getFat() != null ? ingredient.getFat() : 0);
                totalCarbs += (amount / portion) * (ingredient.getCarbs() != null ? ingredient.getCarbs() : 0);
            }
        }

        return new RecipeMacrosDTO(totalCalories, totalProtein, totalFat, totalCarbs);
    }

    public Set<RecipeDTO> findRecipes(String name) {
        Set<Recipe> recipes = recipeRepository.findByNameContainingIgnoreCase(name);
        return recipes.stream()
                    .map(RecipeDTO::new)
                    .collect(Collectors.toSet());
    }


    public RecipeDTO findRecipeByName(String name) {
        Recipe recipe =  recipeRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        return new RecipeDTO(recipe);
    }

    public RecipeDTO findRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        return new RecipeDTO(recipe);
    }

    public Recipe createRecipe(RecipeCreateRequest request) {
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());

        for (RecipeContentDTO dto : request.getContents()) {
            Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

            RecipeContent content = new RecipeContent();
            content.setIngredient(ingredient);
            content.setIngredientAmount(dto.getIngredientAmount());
            content.setRecipe(recipe);

            recipe.getRecipeContents().add(content);
        }

        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(RecipeUpdateRequest request, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());

        recipe.getRecipeContents().clear();

        for (RecipeContentDTO dto : request.getContents()) {
            Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

            RecipeContent content = new RecipeContent();
            content.setIngredient(ingredient);
            content.setIngredientAmount(dto.getIngredientAmount());
            content.setRecipe(recipe);

            recipe.getRecipeContents().add(content);
        }

        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        recipeRepository.delete(recipe);
    }
}
