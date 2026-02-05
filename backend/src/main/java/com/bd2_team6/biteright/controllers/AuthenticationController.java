package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.requests.RegistrationRequest;
import com.bd2_team6.biteright.controllers.requests.CheckAvailabilityRequest;
import com.bd2_team6.biteright.controllers.requests.VerificationRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.EmailUpdateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.PasswordUpdateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.UsernameUpdateRequest;
import com.bd2_team6.biteright.controllers.responses.AvailabilityResponse;
import com.bd2_team6.biteright.authentication.jason_web_token.JwtService;
import com.bd2_team6.biteright.controllers.requests.LoginRequest;
import com.bd2_team6.biteright.controllers.requests.PasswordResetRequest;
import com.bd2_team6.biteright.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authService;
    private final JwtService jwtService;
    
    @GetMapping("/testtoken")
    public ResponseEntity<String> test(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Email associated with given token: " + email);
        return ResponseEntity.status(HttpStatus.OK).body("The token provided is valid.");
    }

    @PostMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(@RequestBody CheckAvailabilityRequest request) {
        try {
            AvailabilityResponse response = authService.checkAvailability(request.getUsername(), request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error checking availability.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking availability.\n" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody RegistrationRequest registrationRequestBody) {
        try {
            authService.registerNewUser(registrationRequestBody);
            return ResponseEntity.status(HttpStatus.OK).body("User registered successfully");
        }
        catch (Exception e) {
            logger.error("Error registering user.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception caught during registration.\n" +e.getMessage());
        }
    } 

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequestBody)  {
        try {
            authService.loginUser(loginRequestBody.getEmail(), loginRequestBody.getPassword());
            String token = jwtService.generateToken(loginRequestBody.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        catch (Exception e) {
            logger.error("Error logging in user.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception caught during login.\n" + e.getMessage());
        }
    }

    @PostMapping("/verifyuser")
    private ResponseEntity<String> verifyUser(@RequestBody VerificationRequest request) {
        try {
            authService.verifyUser(request.getEmail(), request.getCode());
            return ResponseEntity.status(HttpStatus.OK).body("User verified successfully");
        }
        catch (Exception e) {
            logger.error("Error verifying user.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Exception caught during verification.\n" +e.getMessage());
        }
    }

    @GetMapping("/getusers")
    public ResponseEntity<String> getUsers() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.getAllUsers());
        }
        catch (Exception e) {
            logger.error("Error getting users.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Exception caught during getting users.\n" +e.getMessage());
        }
    }

    @PostMapping("/changeusername")
    public ResponseEntity<String> changeUsername(Authentication authentication,  @RequestBody UsernameUpdateRequest request) {
        String email = authentication.getName();
        try {
            authService.changeUsername(email, request.getNewUsername());
            return ResponseEntity.status(HttpStatus.OK).body("Email changed successfully.");
        }
        catch (Exception e) {
            logger.error("Error changing username.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Exception caught while changing username.\n" +e.getMessage());
        }
    }

        @PostMapping("/changeemail")
    public ResponseEntity<String> changeEmail(Authentication authentication,  @RequestBody EmailUpdateRequest request) {
        String oldEmail = authentication.getName();
        try {
            authService.changeEmail(oldEmail, request.getNewEmail());
            String token = jwtService.generateToken(request.getNewEmail());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        catch (Exception e) {
            logger.error("Error changing email.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Exception caught during changing email.\n" +e.getMessage());
        }
    }

    @PostMapping("/changepassword")
    public ResponseEntity<String> changePassword(Authentication authentication, @RequestBody PasswordUpdateRequest request) {
        String email = authentication.getName();
        try {
            System.out.println(email);
            System.out.println(request.getOldPassword());
            authService.changePassword(email, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully.");
        }
        catch (Exception e) {
            logger.error("Error changing password.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Exception caught during changing password.\n" +e.getMessage());
        }
    }

    @PutMapping("/forgottenpassword/{email}")
    public ResponseEntity<String> manageForgottenPassword(@PathVariable("email") String email) {
        try {
            authService.manageForgottenPassword(email);
            return ResponseEntity.status(HttpStatus.OK).body("You have sent you an email with a special link. Please check your inbox.\n");
        } catch (Exception e) {
            logger.error("Error managing forgotten password.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception.\n" +e.getMessage());
        }
    }

    @PostMapping("/resetforgottenpassword")
    public ResponseEntity<String> resetForgottenPassword(@RequestBody PasswordResetRequest request) {
        try {
            authService.verifyForgottenPasswordCode(request.getEmail(), request.getCode());
            authService.resetForgottenPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK).body("You have successfully changed your password.\n");
        }catch (Exception e) {
            logger.error("Error resetting forgotten password.\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failure. " +e.getMessage());
        }
    }
}
