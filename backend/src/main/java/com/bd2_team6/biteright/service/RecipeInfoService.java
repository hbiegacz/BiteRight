package com.bd2_team6.biteright.service;

import org.springframework.stereotype.Service;

import com.bd2_team6.biteright.controllers.requests.create_requests.RecipeInfoCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.RecipeInfoUpdateRequest;
import com.bd2_team6.biteright.entities.recipe_info.RecipeInfo;
import com.bd2_team6.biteright.entities.recipe_info.RecipeInfoRepository;

@Service
public class RecipeInfoService {
    private final RecipeInfoRepository recipeInfoRepository;

    public RecipeInfoService(RecipeInfoRepository recipeInfoRepository) {
        this.recipeInfoRepository = recipeInfoRepository;
    }

    public RecipeInfo findRecipeInfoByName(String recipeName) {
        RecipeInfo recipeInfo = recipeInfoRepository.findByRecipeName(recipeName)
                .orElseThrow(() -> new IllegalArgumentException("Recipe info not found"));
        return recipeInfo;
    }

    public RecipeInfo findRecipeInfoById(Long recipeId) {
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe info not found"));
        return recipeInfo;
    }

    public RecipeInfo createRecipeInfo(RecipeInfoCreateRequest request) {        
        RecipeInfo newRecipeInfo = new RecipeInfo();
        newRecipeInfo.setCalories(request.getCalories());
        newRecipeInfo.setCarbs(request.getCarbs());
        newRecipeInfo.setFat(request.getCarbs());
        newRecipeInfo.setRecipeName(request.getRecipeName());
        newRecipeInfo.setProtein(request.getProtein());

        return recipeInfoRepository.save(newRecipeInfo);
    }

    public RecipeInfo updateRecipeInfo(Long recipeId, RecipeInfoUpdateRequest request) {
        RecipeInfo newRecipeInfo = recipeInfoRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe info not found"));

        newRecipeInfo.setCalories(request.getCalories());
        newRecipeInfo.setCarbs(request.getCarbs());
        newRecipeInfo.setFat(request.getCarbs());
        newRecipeInfo.setRecipeName(request.getRecipeName());
        newRecipeInfo.setProtein(request.getProtein());

        return recipeInfoRepository.save(newRecipeInfo);
    }

    public void deleteRecipeInfo(Long recipeId) {
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Meal info not found"));

        recipeInfoRepository.delete(recipeInfo);
    }
}
