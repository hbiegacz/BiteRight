package com.bd2_team6.biteright.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2_team6.biteright.controllers.requests.create_requests.RecipeInfoCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.RecipeInfoUpdateRequest;
import com.bd2_team6.biteright.entities.recipe_info.RecipeInfo;
import com.bd2_team6.biteright.service.RecipeInfoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/recipeInfo")
@RequiredArgsConstructor
public class RecipeInfoController {
    private final RecipeInfoService recipeInfoService;

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findRecipeInfoByName(@PathVariable("name") String recipeName) {
        try {
            RecipeInfo recipeInfo = recipeInfoService.findRecipeInfoByName(recipeName);
            return ResponseEntity.ok(recipeInfo);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findRecipeInfoById(@PathVariable("id") Long recipeId) {
        try {
            RecipeInfo recipeInfo = recipeInfoService.findRecipeInfoById(recipeId);
            return ResponseEntity.ok(recipeInfo);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRecipeInfo(@RequestBody RecipeInfoCreateRequest request) {
        try {
            RecipeInfo recipeInfo = recipeInfoService.createRecipeInfo(request);
            return ResponseEntity.ok(recipeInfo);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRecipeInfo(@PathVariable("id") Long recipeId,
            @RequestBody RecipeInfoUpdateRequest request) {
        try {
            RecipeInfo recipeInfo = recipeInfoService.updateRecipeInfo(recipeId, request);
            return ResponseEntity.ok(recipeInfo);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecipeInfo(@PathVariable("id") Long recipeId) {
        try {
            recipeInfoService.deleteRecipeInfo(recipeId);
            return ResponseEntity.ok("Recipe info deleted successfully");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
