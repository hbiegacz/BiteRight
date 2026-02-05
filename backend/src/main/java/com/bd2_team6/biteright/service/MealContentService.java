package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.DTO.MealContentDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.MealContentCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.MealContentUpdateRequest;
import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.meal.Meal;
import com.bd2_team6.biteright.entities.meal_content.MealContent;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;
import com.bd2_team6.biteright.entities.meal.MealRepository;
import com.bd2_team6.biteright.entities.meal_content.MealContentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealContentService {

    private final MealContentRepository mealContentRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;

    public Set<MealContentDTO> findMealContentById(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        return meal.getMealContents().stream()
                .map(MealContentDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<MealContentDTO> findMealContentByName(String mealName) {
        Meal meal = mealRepository.findByName(mealName)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        return meal.getMealContents().stream()
                .map(MealContentDTO::new)
                .collect(Collectors.toSet());
    }

    public MealContentDTO addContentToMeal(MealContentCreateRequest request) {
        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        MealContent content = new MealContent(ingredient, meal, request.getIngredientAmount());
        return new MealContentDTO(mealContentRepository.save(content));
    }

    public MealContentDTO updateContent(Long id, MealContentUpdateRequest request) {
        MealContent content = mealContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meal content not found"));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        content.setIngredient(ingredient);
        content.setIngredientAmount(request.getIngredientAmount());
        return new MealContentDTO(mealContentRepository.save(content));
    }

    public void deleteMealContent(Long id) {
        MealContent content = mealContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meal content not found"));

        mealContentRepository.delete(content);
    }
}
