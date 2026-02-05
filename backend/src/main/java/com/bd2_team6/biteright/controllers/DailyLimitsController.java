package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.DailyLimitsDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.DailyLimitsCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.DailyLimitsUpdateRequest;
import com.bd2_team6.biteright.entities.daily_limits.DailyLimits;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.service.DailyLimitsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dailyLimits")
@RequiredArgsConstructor
public class DailyLimitsController {
    private static final Logger logger = LoggerFactory.getLogger(DailyLimitsController.class);
    private final DailyLimitsService dailyLimitsService;
    private final UserRepository userRepository;

    @GetMapping("/find")
    public ResponseEntity<?> findDailyLimits(Authentication authentication) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            DailyLimits dailyLimits = dailyLimitsService.findDailyLimitsByUsername(username);
            DailyLimitsDTO dailyLimitsDTO = new DailyLimitsDTO(dailyLimits.getDailyLimitId(), dailyLimits.getCalorieLimit(), dailyLimits.getProteinLimit(), 
            dailyLimits.getFatLimit(), dailyLimits.getCarbLimit(), dailyLimits.getWaterGoal());
            return ResponseEntity.ok(dailyLimitsDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding daily limits.\n" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> addDailyLimits(Authentication authentication, @RequestBody DailyLimitsCreateRequest request) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            DailyLimits newDailyLimits = dailyLimitsService.createDailyLimits(username, request);
            DailyLimitsDTO newDailyLimitsDTO = new DailyLimitsDTO(newDailyLimits.getDailyLimitId(), newDailyLimits.getCalorieLimit(), newDailyLimits.getProteinLimit(), 
            newDailyLimits.getFatLimit(), newDailyLimits.getCarbLimit(), newDailyLimits.getWaterGoal());
            return ResponseEntity.ok(newDailyLimitsDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error creating daily limits.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateDailyLimits(Authentication authentication, @RequestBody DailyLimitsUpdateRequest request) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            DailyLimits updatedDailyLimits = dailyLimitsService.updateDailyLimits(username, request);
            DailyLimitsDTO updatedDailyLimitsDTO = new DailyLimitsDTO(updatedDailyLimits.getDailyLimitId(), updatedDailyLimits.getCalorieLimit(), updatedDailyLimits.getProteinLimit(), 
            updatedDailyLimits.getFatLimit(), updatedDailyLimits.getCarbLimit(), updatedDailyLimits.getWaterGoal());
            return ResponseEntity.ok(updatedDailyLimitsDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error updating daily limits.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
