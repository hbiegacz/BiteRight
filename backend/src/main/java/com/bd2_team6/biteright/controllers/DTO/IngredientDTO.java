package com.bd2_team6.biteright.controllers.DTO;

import com.bd2_team6.biteright.entities.ingredient.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private Long id;
    private String name;
    private String brand;
    private Integer portionSize;
    private Integer calories;
    private Integer protein;
    private Integer fat;
    private Integer carbs;

    public IngredientDTO(Ingredient ingredient) {
        this.id = ingredient.getIngredientId();
        this.name = ingredient.getName();
        this.brand = ingredient.getBrand();
        this.portionSize = ingredient.getPortionSize();
        this.calories = ingredient.getCalories();
        this.protein = ingredient.getProtein();
        this.fat = ingredient.getFat();
        this.carbs = ingredient.getCarbs();
    }
}
