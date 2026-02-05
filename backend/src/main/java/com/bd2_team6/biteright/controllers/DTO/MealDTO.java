package com.bd2_team6.biteright.controllers.DTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.bd2_team6.biteright.entities.meal.Meal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime mealDate;
    private String mealTypeName;
    private Long mealTypeId;
    private Set<MealContentDTO> contents;

    public MealDTO(Meal meal) {
        this.id = meal.getMealId();
        this.name = meal.getName();
        this.description = meal.getDescription();
        this.mealDate = meal.getMealDate();
        this.mealTypeName = meal.getMealType().getName();
        this.mealTypeId = meal.getMealType().getTypeId();

        this.contents = meal.getMealContents().stream()
                             .map(MealContentDTO::new)
                             .collect(Collectors.toSet());
    }
}
