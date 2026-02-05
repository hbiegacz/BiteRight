package com.bd2_team6.biteright.controllers.DTO;

import com.bd2_team6.biteright.entities.recipe.Recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RecipeDTO {
    private Long recipeId;
    private String name;
    private String description;
    private Set<RecipeContentDTO> contents;

    public RecipeDTO(Recipe recipe) {
        this.recipeId = recipe.getRecipeId();
        this.name = recipe.getName();
        this.description = recipe.getDescription();
        this.contents = recipe.getRecipeContents().stream()
                .map(RecipeContentDTO::new)
                .collect(Collectors.toSet());
    }
}