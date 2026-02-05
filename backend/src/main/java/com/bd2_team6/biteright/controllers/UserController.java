package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.UserDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/find")
    public ResponseEntity<?> findUser(Authentication authentication) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            User user = userService.getUserByName(username);
            return ResponseEntity.ok(mapToDTO(user));
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding user." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding user." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(), user.getType(),
                user.getIsVerified(), user.getForgottenPasswordCode());
    }
}
