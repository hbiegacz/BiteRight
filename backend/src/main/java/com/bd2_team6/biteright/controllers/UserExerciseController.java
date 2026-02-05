package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.UserExerciseDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.UserExerciseCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.UserExerciseUpdateRequest;
import com.bd2_team6.biteright.entities.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bd2_team6.biteright.entities.user_exercise.UserExercise;
import com.bd2_team6.biteright.service.UserExerciseService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@RestController
@RequestMapping("/userExercise")
@RequiredArgsConstructor
public class UserExerciseController {
    private final UserExerciseService userExerciseService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createUserExercise(Authentication authentication,
                                               @RequestBody UserExerciseCreateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserExercise userExercise = userExerciseService.createUserExercise(username, request);
            return ResponseEntity.ok(mapToDTO(userExercise));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findExercisesForUser")
    public ResponseEntity<?> findExerciseForUser(Authentication authentication,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "desc") String sortDir,
                                                    @RequestParam(defaultValue = "activityDate") String sortBy) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserExercise> userExercises = userExerciseService.findUserExercisesByUsername(username, pageable);
            return ResponseEntity.ok(mapToDTOPage(userExercises));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findExercisesByDate/{date}")
    public ResponseEntity<?> findExercisesByDate(Authentication authentication,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "desc") String sortDir,
                                                    @RequestParam(defaultValue = "activityDate") String sortBy,
                                                    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserExercise> userExercises = userExerciseService.findUserExercisesByDate(username, date, pageable);
            return ResponseEntity.ok(mapToDTOPage(userExercises));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findLastExercise")
    public ResponseEntity<?> findLastExerciseByDate(Authentication authentication) {

        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserExercise userExercise = userExerciseService.findLastExerciseByUsername(username);
            return ResponseEntity.ok(mapToDTO(userExercise));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findExerciseById/{id}")
    public ResponseEntity<?> findExerciseById(Authentication authentication, @PathVariable("id") Long userExerciseId) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserExercise userExercise = userExerciseService.findExerciseById(username, userExerciseId);
            return ResponseEntity.ok(mapToDTO(userExercise));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExerciseById(Authentication authentication,
            @PathVariable("id") Long userExerciseId,
                                                   @RequestBody UserExerciseUpdateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            UserExercise updatedUserExercise = userExerciseService.updateUserExerciseById(username, userExerciseId,
                    request);
            return ResponseEntity.ok(mapToDTO(updatedUserExercise));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExercise(Authentication authentication, @PathVariable("id") Long userExerciseId) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            userExerciseService.deleteUserExerciseById(username, userExerciseId);
            return ResponseEntity.ok("User exercise deleted successfully");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private UserExerciseDTO mapToDTO(UserExercise userExercise) {
        return new UserExerciseDTO(userExercise.getUserExerciseId(), userExercise.getUser().getId(),
                userExercise.getExerciseInfo().getExerciseId(), userExercise.getActivityDate(),
                userExercise.getDuration(), userExercise.getCaloriesBurnt(), userExercise.getExerciseInfo().getName());
    }

    private Page<UserExerciseDTO> mapToDTOPage(Page<UserExercise> page) {
        return page.map(this::mapToDTO);
    }
}
