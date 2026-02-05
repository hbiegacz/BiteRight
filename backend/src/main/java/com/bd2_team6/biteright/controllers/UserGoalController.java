package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.UserGoalDTO;
import com.bd2_team6.biteright.controllers.requests.update_requests.UserGoalUpdateRequest;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.user_goal.UserGoal;
import com.bd2_team6.biteright.service.UserGoalService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/userGoal")
@RequiredArgsConstructor
public class UserGoalController {
    private static final Logger logger = LoggerFactory.getLogger(UserGoalController.class);

    private final UserGoalService userGoalService;
    private final UserRepository userRepository;

    @GetMapping("/findUserGoal")
    public ResponseEntity<?> findUserGoal(Authentication authentication) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserGoal userGoal = userGoalService.findUserGoalByUsername(username);
            UserGoalDTO userGoalDTO = new UserGoalDTO(userGoal.getUserGoalId(), userGoal.getGoalType(),
                    userGoal.getGoalWeight(), userGoal.getDeadline());
            return ResponseEntity.ok(userGoalDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding user goal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding user goal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserGoal(Authentication authentication, @RequestBody UserGoalUpdateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserGoal updatedGoal = userGoalService.updateUserGoal(username, request);
            UserGoalDTO userGoalDTO = new UserGoalDTO(updatedGoal.getUserGoalId(), updatedGoal.getGoalType(),
                    updatedGoal.getGoalWeight(), updatedGoal.getDeadline());
            return ResponseEntity.ok(userGoalDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error updating user goal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user goal." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
