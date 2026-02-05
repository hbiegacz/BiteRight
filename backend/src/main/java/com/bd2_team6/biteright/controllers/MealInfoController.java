package com.bd2_team6.biteright.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bd2_team6.biteright.controllers.requests.create_requests.MealInfoCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.MealInfoUpdateRequest;
import com.bd2_team6.biteright.entities.meal_info.MealInfo;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.service.MealInfoService;

@RestController
@RequestMapping("/mealInfo")
@RequiredArgsConstructor
public class MealInfoController {
    private static final Logger logger = LoggerFactory.getLogger(MealInfoController.class);
    private final MealInfoService mealInfoService;
    private final UserRepository userRepository;

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findMealInfoById(@PathVariable("id") Long mealId) {
        try {
            MealInfo mealInfo = mealInfoService.findMealInfoById(mealId);
            return ResponseEntity.ok(mealInfo);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding meal info by id." + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error finding meal info by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findMealInfoByName(@PathVariable("name") String mealName) {
        try {
            MealInfo mealInfo = mealInfoService.findMealInfoByName(mealName);
            return ResponseEntity.ok(mealInfo);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding meal info by name." + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error finding meal info by name." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMealInfo(Authentication authentication, @RequestBody MealInfoCreateRequest request) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        try {
            MealInfo mealInfo = mealInfoService.createMealInfo(username, request);
            return ResponseEntity.ok(mealInfo);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error creating meal info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating meal info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMealInfo(@PathVariable("id") Long mealId,
            @RequestBody MealInfoUpdateRequest request) {
        try {
            MealInfo mealInfo = mealInfoService.updateMealInfo(mealId, request);
            return ResponseEntity.ok(mealInfo);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error updating meal info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating meal info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMealInfo(@PathVariable("id") Long mealId) {
        try {
            mealInfoService.deleteMealInfo(mealId);
            return ResponseEntity.ok("Meal info deleted successfully");
        }
        catch (IllegalArgumentException e) {
            logger.error("Error deleting meal info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting meal info." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
