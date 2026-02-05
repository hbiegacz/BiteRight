package com.bd2_team6.biteright.entities.recipe_info;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.*;

@Entity
@Immutable
@Table(name = "recipe_info")
@Getter
@Setter
@NoArgsConstructor
public class RecipeInfo {
    @Id
    @Column(name = "recipe_id")
    private Long recipeId;
    
    @Column(name = "recipe_name")
    private String recipeName;
    
    @Column(name = "calories")
    private Double calories;
    
    @Column(name = "protein")
    private Double protein;
    
    @Column(name = "fat")
    private Double fat;
    
    @Column(name = "carbs")
    private Double carbs;
    
    public RecipeInfo(String recipeName, Double calories, Double protein, Double fat, Double carbs) {
        this.recipeName = recipeName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
}
