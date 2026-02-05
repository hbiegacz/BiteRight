package com.bd2_team6.biteright.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bd2_team6.biteright.controllers.DTO.MealTypeDTO;
import com.bd2_team6.biteright.entities.meal_type.MealType;
import com.bd2_team6.biteright.service.MealTypeService;

@RestController
@RequestMapping("/mealType")
@RequiredArgsConstructor
public class MealTypeController {
    private static final Logger logger = LoggerFactory.getLogger(MealTypeController.class);
    private final MealTypeService mealTypeService;

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findMealTypeById(@PathVariable("id") Long mealId) {
        try {
            MealType mealType = mealTypeService.findMealTypeById(mealId);
            MealTypeDTO mealTypeDTO = new MealTypeDTO(mealType.getTypeId(), mealType.getName());
            return ResponseEntity.ok(mealTypeDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding meal type by id." + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error finding meal type by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
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
            logger.error("Error finding meal type by name." + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error finding meal type by name." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
