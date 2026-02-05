package com.bd2_team6.biteright.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bd2_team6.biteright.controllers.DTO.IngredientDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.IngredientCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.IngredientUpdateRequest;
import com.bd2_team6.biteright.entities.ingredient.Ingredient;
import com.bd2_team6.biteright.entities.ingredient.IngredientRepository;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Set<IngredientDTO> findIngredientsByName(String name) {
        Set<Ingredient> ingredients = ingredientRepository.findByNameContainingIgnoreCase(name);
        return ingredients.stream() 
                .map(IngredientDTO::new)
                .collect(Collectors.toSet());
    }

    public Ingredient createIngredient(IngredientCreateRequest request) {
        Ingredient newIngredient = new Ingredient();
        newIngredient.setName(request.getName());
        newIngredient.setBrand(request.getBrand());
        newIngredient.setPortionSize(request.getPortionSize());
        newIngredient.setCalories(request.getCalories());
        newIngredient.setProtein(request.getProtein());
        newIngredient.setFat(request.getFat());
        newIngredient.setCarbs(request.getCarbs());

        return ingredientRepository.save(newIngredient);
    }
    
    public Ingredient updateIngredient(Long id, IngredientUpdateRequest request) {
        Ingredient newIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        newIngredient.setName(request.getName());
        newIngredient.setBrand(request.getBrand());
        newIngredient.setPortionSize(request.getPortionSize());
        newIngredient.setCalories(request.getCalories());
        newIngredient.setProtein(request.getProtein());
        newIngredient.setFat(request.getFat());
        newIngredient.setCarbs(request.getCarbs());

        return ingredientRepository.save(newIngredient);
    }

    public void deleteIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        ingredientRepository.delete(ingredient);
    }
}
