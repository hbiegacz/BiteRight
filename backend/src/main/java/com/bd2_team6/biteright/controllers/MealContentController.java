package com.bd2_team6.biteright.controllers;

import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bd2_team6.biteright.controllers.DTO.MealContentDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.MealContentCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.MealContentUpdateRequest;
import com.bd2_team6.biteright.service.MealContentService;

@RestController
@RequestMapping("/mealContent")
@RequiredArgsConstructor
public class MealContentController {
    private final MealContentService mealContentService;

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findMealContentByName(@PathVariable("name") String mealName) {
        try {
            Set<MealContentDTO> mealContents = mealContentService.findMealContentByName(mealName);
            return ResponseEntity.ok(mealContents);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findMealContentById(@PathVariable("id") Long mealId) {
        try {
            Set<MealContentDTO> mealContent = mealContentService.findMealContentById(mealId);
            return ResponseEntity.ok(mealContent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMealContent(@RequestBody MealContentCreateRequest request) {
        try {
            MealContentDTO mealContent = mealContentService.addContentToMeal(request);
            return ResponseEntity.ok(mealContent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMealContent(@PathVariable("id") Long contentId,
                                    @RequestBody MealContentUpdateRequest request) {
        try {
            MealContentDTO mealContent = mealContentService.updateContent(contentId, request);
            return ResponseEntity.ok(mealContent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMealContent(@PathVariable("id") Long id) {
        try {
            mealContentService.deleteMealContent(id);
            return ResponseEntity.ok("Meal content successfully deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
