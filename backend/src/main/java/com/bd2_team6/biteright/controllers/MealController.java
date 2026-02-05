package com.bd2_team6.biteright.controllers;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bd2_team6.biteright.controllers.DTO.MealDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.MealCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.MealUpdateRequest;
import com.bd2_team6.biteright.entities.meal.Meal;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.service.MealService;

@RestController
@RequestMapping("/meal")
@RequiredArgsConstructor
public class MealController {
    private static final Logger logger = LoggerFactory.getLogger(MealController.class);
    private final MealService mealService;
    private final UserRepository userRepository;


    @GetMapping("/findUserMeals")
    public ResponseEntity<?> findUserMeals(Authentication authentication) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            Set<MealDTO> mealsDTO = mealService.findUserMealsByUsername(username);
            return ResponseEntity.ok(mealsDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding user meals." + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error finding user meals." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findByDate/{date}") 
    public ResponseEntity<?> findMealsByDate(Authentication authentication, 
                                             @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            Set<MealDTO> mealsDTO = mealService.findMealsByDate(username, date);
            return ResponseEntity.ok(mealsDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding meals by date." + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error finding meals by date." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<?> findMealByName(Authentication authentication, @PathVariable("name") String mealName) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            MealDTO mealDTO = mealService.findMealByName(username, mealName);
            return ResponseEntity.ok(mealDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding meal by name." + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error finding meal by name." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findByID/{id}")
    public ResponseEntity<?> findMealById(Authentication authentication, @PathVariable("id") Long mealId) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            MealDTO mealDTO = mealService.findMealById(username, mealId);
            return ResponseEntity.ok(mealDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding meal by id." + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error finding meal by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create") 
    public ResponseEntity<?> createMeal(Authentication authentication, @RequestBody MealCreateRequest request) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            Meal meal = mealService.createMeal(username, request);
            MealDTO mealDTO = new MealDTO(meal);
            return ResponseEntity.ok(mealDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error creating meal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating meal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMeal(Authentication authentication, @RequestBody MealUpdateRequest request, 
            @PathVariable("id") Long mealId) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            Meal meal = mealService.updateMeal(username, request, mealId);
            MealDTO mealDTO = new MealDTO(meal);
            return ResponseEntity.ok(mealDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error updating meal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating meal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMeal(Authentication authentication, @PathVariable("id") Long mealId) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            mealService.deleteMeal(username, mealId);
            return ResponseEntity.ok("Meal successfully deleted");
        }
        catch (IllegalArgumentException e) {
            logger.error("Error deleting meal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting meal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
