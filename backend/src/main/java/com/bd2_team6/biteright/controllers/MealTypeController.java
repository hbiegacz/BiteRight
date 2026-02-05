package com.bd2_team6.biteright.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bd2_team6.biteright.controllers.DTO.MealTypeDTO;
import com.bd2_team6.biteright.entities.meal_type.MealType;
import com.bd2_team6.biteright.service.MealTypeService;

@RestController
@RequestMapping("/mealType")
@RequiredArgsConstructor
public class MealTypeController {
    private final MealTypeService mealTypeService;

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findMealTypeById(@PathVariable("id") Long mealId) {
        try {
            MealType mealType = mealTypeService.findMealTypeById(mealId);
            MealTypeDTO mealTypeDTO = new MealTypeDTO(mealType.getTypeId(), mealType.getName());
            return ResponseEntity.ok(mealTypeDTO);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findMealTypeByName(@PathVariable("name") String mealTypeName) {
        try {
            MealType mealType = mealTypeService.findMealTypeByName(mealTypeName);
            MealTypeDTO mealTypeDTO = new MealTypeDTO(mealType.getTypeId(), mealType.getName());
            return ResponseEntity.ok(mealTypeDTO);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
