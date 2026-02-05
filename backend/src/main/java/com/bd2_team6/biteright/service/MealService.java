package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.DTO.MealContentDTO;
import com.bd2_team6.biteright.controllers.DTO.MealDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.MealCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.MealUpdateRequest;
import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;
import com.bd2_team6.biteright.entities.meal.Meal;
import com.bd2_team6.biteright.entities.meal.MealRepository;
import com.bd2_team6.biteright.entities.meal_content.MealContent;
import com.bd2_team6.biteright.entities.meal_type.MealType;
import com.bd2_team6.biteright.entities.meal_type.MealTypeRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealService {
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final MealTypeRepository mealTypeRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public MealService(UserRepository userRepository, MealRepository mealRepository, MealTypeRepository mealTypeRepository, 
                        IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.mealTypeRepository = mealTypeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Set<MealDTO> findUserMealsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return user.getMeals().stream()
                .map(MealDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<MealDTO> findMealsByDate(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        Set<Meal> meals = mealRepository.findAllByUserAndMealDateBetween(user, startOfDay, endOfDay);

        return meals.stream()
            .map(MealDTO::new)
            .collect(Collectors.toSet());
    }

    public MealDTO findMealByName(String username, String mealName) {
        Meal meal = mealRepository.findByUsernameAndName(username, mealName)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        return new MealDTO(meal);
    }

    public MealDTO findMealById(String username, Long mealId) {
        Meal meal = mealRepository.findByUsernameAndMealId(username, mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        return new MealDTO(meal);
    }

    public Meal createMeal(String username, MealCreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MealType mealType = mealTypeRepository.findById(request.getMealTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Meal type not found"));

        Meal newMeal = new Meal();
        newMeal.setUser(user);
        newMeal.setMealType(mealType);

        LocalDateTime mealDate = request.getMealDate() != null
            ? request.getMealDate()
            : LocalDateTime.now();
        newMeal.setMealDate(mealDate);

        newMeal.setName(request.getName());
        newMeal.setDescription(request.getDescription());

        for (MealContentDTO contentDTO : request.getContents()) {
            Ingredient ingredient = ingredientRepository.findById(contentDTO.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

            MealContent content = new MealContent();

            content.setIngredient(ingredient);
            content.setMeal(newMeal);
            content.setIngredientAmount(contentDTO.getIngredientAmount());

            newMeal.getMealContents().add(content);
        }

        user.getMeals().add(newMeal);
        return mealRepository.save(newMeal);
    }

    public Meal updateMeal(String username, MealUpdateRequest request, Long mealId) {
        MealType mealType = mealTypeRepository.findById(request.getMealTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Meal type not found"));

        Meal newMeal = mealRepository.findByUsernameAndMealId(username, mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        newMeal.setMealType(mealType);
        newMeal.setName(request.getName());
        newMeal.setDescription(request.getDescription());

        newMeal.getMealContents().clear();

        for (MealContentDTO contentDTO : request.getContents()) {
            Ingredient ingredient = ingredientRepository.findById(contentDTO.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

            MealContent content = new MealContent();

            content.setIngredient(ingredient);
            content.setMeal(newMeal);
            content.setIngredientAmount(contentDTO.getIngredientAmount());

            newMeal.getMealContents().add(content);
        }

        return mealRepository.save(newMeal);
    }

    public void deleteMeal(String username, Long mealId) {
        Meal meal = mealRepository.findByUsernameAndMealId(username, mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        mealRepository.delete(meal);
    }
}