package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.DTO.RecipeContentDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.RecipeContentCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.RecipeContentUpdateRequest;
import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.recipe.Recipe;
import com.bd2_team6.biteright.entities.recipe_content.RecipeContent;
import com.bd2_team6.biteright.entities.recipe_content.RecipeContentRepository;
import com.bd2_team6.biteright.entities.recipe.RecipeRepository;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeContentService {

    private final RecipeContentRepository recipeContentRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public Set<RecipeContentDTO> findRecipeContentById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        return recipe.getRecipeContents().stream()
                .map(RecipeContentDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<RecipeContentDTO> findRecipeContentByName(String recipeName) {
        Recipe recipe = recipeRepository.findByName(recipeName)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        return recipe.getRecipeContents().stream()
                .map(RecipeContentDTO::new)
                .collect(Collectors.toSet());
    }

    public RecipeContentDTO addContentToRecipe(RecipeContentCreateRequest request) {
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        RecipeContent content = new RecipeContent(recipe, ingredient, request.getIngredientAmount());
        return new RecipeContentDTO(recipeContentRepository.save(content));
    }

    public RecipeContentDTO updateContent(Long id, RecipeContentUpdateRequest request) {
        RecipeContent content = recipeContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe content not found"));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        content.setIngredient(ingredient);
        content.setIngredientAmount(request.getIngredientAmount());
        return new RecipeContentDTO(recipeContentRepository.save(content));
    }

    public void deleteRecipeContent(Long id) {
        RecipeContent content = recipeContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe content not found"));
        recipeContentRepository.delete(content);
    }
}

