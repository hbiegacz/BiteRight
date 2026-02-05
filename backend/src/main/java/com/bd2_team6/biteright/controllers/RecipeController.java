package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.RecipeDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.RecipeCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.RecipeUpdateRequest;
import com.bd2_team6.biteright.entities.recipe.Recipe;
import com.bd2_team6.biteright.service.RecipeService;

import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    // Returns recipes that contain provided string (i.e. if you write 'ch' it will return recipes containing 
    // 'ch' like chicken or chili)
    @GetMapping("/findRecipes/{name}")
    public ResponseEntity<?> findRecipes(@PathVariable("name") String name) {
        try {
            Set<RecipeDTO> recipesDTO = recipeService.findRecipes(name);
            return ResponseEntity.ok(recipesDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findRecipeByName(@PathVariable("name") String recipeName) {
        try {
            RecipeDTO recipeDTO = recipeService.findRecipeByName(recipeName);
            return ResponseEntity.ok(recipeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findRecipeByName(@PathVariable("id") Long recipeId) {
        try {
            RecipeDTO recipeDTO = recipeService.findRecipeById(recipeId);
            return ResponseEntity.ok(recipeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRecipe(@RequestBody RecipeCreateRequest request) {
        try {
            Recipe recipe = recipeService.createRecipe(request);
            RecipeDTO recipeDTO = new RecipeDTO(recipe);
            return ResponseEntity.ok(recipeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRecipe(@RequestBody RecipeUpdateRequest request,
            @PathVariable("id") Long recipeId) {
        try {
            Recipe updated = recipeService.updateRecipe(request, recipeId);
            RecipeDTO recipeDTO = new RecipeDTO(updated);
            return ResponseEntity.ok(recipeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") Long recipeId) {
        try {
            recipeService.deleteRecipe(recipeId);
            return ResponseEntity.ok("Recipe successfully deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

