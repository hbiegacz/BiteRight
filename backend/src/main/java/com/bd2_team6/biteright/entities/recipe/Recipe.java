package com.bd2_team6.biteright.entities.recipe;

import com.bd2_team6.biteright.entities.recipe_content.RecipeContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private Set<RecipeContent> recipeContents = new HashSet<>();

    public Recipe(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Recipe(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
