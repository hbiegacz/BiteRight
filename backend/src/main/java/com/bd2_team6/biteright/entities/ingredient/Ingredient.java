package com.bd2_team6.biteright.entities.ingredient;

import com.bd2_team6.biteright.entities.meal_content.MealContent;
import com.bd2_team6.biteright.entities.recipe_content.RecipeContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "ingredient")
@Getter
@Setter
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long ingredientId;
    
    @Column(name = "name")
    private String name;       
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "portion_size")
    private Integer portionSize;
    
    @Column(name = "calories")
    private Integer calories;
    
    @Column(name = "protein")
    private Integer protein;      
    
    @Column(name = "fat")
    private Integer fat; 
    
    @Column(name = "carbs")
    private Integer carbs;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private Set<MealContent> mealContents = new HashSet<>();

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private Set<RecipeContent> recipeContents = new HashSet<>();

    public Ingredient(String name, String brand, Integer portionSize, Integer calories, Integer protein, Integer fat,
                      Integer carbs) {
        this.name = name;
        this.brand = brand;
        this.portionSize = portionSize;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
}
