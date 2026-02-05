package com.bd2_team6.biteright.controllers.DTO;

import com.bd2_team6.biteright.entities.recipe_content.RecipeContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RecipeContentDTO {
    private Long recipeContentId;
    private Long ingredientId;
    private String ingredientName;
    private Integer ingredientAmount;

    public RecipeContentDTO(RecipeContent content) {
        this.recipeContentId = content.getRecipeContentId();
        this.ingredientId = content.getIngredient().getIngredientId();
        this.ingredientName = content.getIngredient().getName();
        this.ingredientAmount = content.getIngredientAmount();
    }
}
