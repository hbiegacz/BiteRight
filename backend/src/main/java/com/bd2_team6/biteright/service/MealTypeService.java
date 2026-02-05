package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.entities.meal_type.MealType;
import com.bd2_team6.biteright.entities.meal_type.MealTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealTypeService {
    private final MealTypeRepository mealTypeRepository;

    @Autowired
    public MealTypeService(MealTypeRepository mealTypeRepository) {
        this.mealTypeRepository = mealTypeRepository;
    }

    public MealType findMealTypeById(Long mealId) {
        MealType mealType = mealTypeRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal type not found"));
        return mealType;
    }

    public MealType findMealTypeByName(String mealTypeName) {
        MealType mealType = mealTypeRepository.findByName(mealTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Meal type not found"));
        return mealType;
    }
}
