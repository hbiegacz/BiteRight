package com.bd2_team6.biteright.controllers.DTO;

import com.bd2_team6.biteright.entities.meal_content.MealContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealContentDTO {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Integer ingredientAmount;

    public MealContentDTO(MealContent content) {
        this.id = content.getMealContentId();
        this.ingredientId = content.getIngredient().getIngredientId();
        this.ingredientName = content.getIngredient().getName();
        this.ingredientAmount = content.getIngredientAmount();
    }
}
