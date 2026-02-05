package com.bd2_team6.biteright.entities.recipe_content;

import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.recipe.Recipe;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipe_content")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RecipeContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_content_id")
    private Long recipeContentId;

    @Column(name = "ingredient_amount")
    private Integer ingredientAmount;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeContent(Recipe recipe, Ingredient ingredient, Integer ingredientAmount) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.ingredientAmount = ingredientAmount;
    }
}
