package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.UserPreferencesDTO;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.user_preferences.UserPreferences;
import com.bd2_team6.biteright.service.UserPreferencesService;
import com.bd2_team6.biteright.controllers.requests.update_requests.UserPreferencesUpdateRequest;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userPreferences")
@RequiredArgsConstructor
public class UserPreferencesController {
    private static final Logger logger = LoggerFactory.getLogger(UserPreferencesController.class);
    private final UserPreferencesService userPreferencesService;
    private final UserRepository userRepository;

    @GetMapping("/findUserPreferences")
    public ResponseEntity<?> findUserPreferences(Authentication authentication) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserPreferences userPreferences = userPreferencesService.findUserPreferencesByUsername(username);
            UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(userPreferences.getUserPreferencesId(),
                    userPreferences.getLanguage(), userPreferences.getDarkmode(), userPreferences.getFont(),
                    userPreferences.getNotifications());
            return ResponseEntity.ok(userPreferencesDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding user preferences." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding user preferences." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserPreferences(Authentication authentication,
                                                   @RequestBody UserPreferencesUpdateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserPreferences userPreferences = userPreferencesService.updateUserPreferences(username, request);
            UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(userPreferences.getUserPreferencesId(),
                    userPreferences.getLanguage(), userPreferences.getDarkmode(), userPreferences.getFont(),
                    userPreferences.getNotifications());
            return ResponseEntity.ok(userPreferencesDTO);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error updating user preferences." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user preferences." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
