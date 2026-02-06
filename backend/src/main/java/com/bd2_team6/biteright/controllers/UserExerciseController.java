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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

@RestController
@RequestMapping("/userExercise")
@RequiredArgsConstructor
public class UserExerciseController {
    private final UserExerciseService userExerciseService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserExerciseController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createUserExercise(Authentication authentication,
            @RequestBody UserExerciseCreateRequest request) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to create UserExercise for user: {}", username);
        try {
            UserExercise userExercise = userExerciseService.createUserExercise(username, request);
            return ResponseEntity.ok(mapToDTO(userExercise));
        }
        catch (IllegalArgumentException e){
            logger.error("Bad Request: Failed to create user exercise: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure during exercise creation", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findExercisesForUser")
    public ResponseEntity<?> findExerciseForUser(Authentication authentication,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "activityDate") String sortBy) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to get all exercises for user: {}, page: {}", username, page);
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserExercise> userExercises = userExerciseService.findUserExercisesByUsername(username, pageable);
            return ResponseEntity.ok(mapToDTOPage(userExercises));
        }
        catch (IllegalArgumentException e){
            logger.error("Search failed for user: {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure finding user exercises", e);
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
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to get exercises for user: {}, date: {}", username, date);
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserExercise> userExercises = userExerciseService.findUserExercisesByDate(username, date, pageable);
            return ResponseEntity.ok(mapToDTOPage(userExercises));
        } catch (IllegalArgumentException e) {
            logger.error("Date search failed for user: {}, date: {}: {}", username, date, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure finding exercises by date", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findLastExercise")
    public ResponseEntity<?> findLastExerciseByDate(Authentication authentication) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to get last exercise for user: {}", username);
        try {
            UserExercise userExercise = userExerciseService.findLastExerciseByUsername(username);
            return ResponseEntity.ok(mapToDTO(userExercise));
        }
        catch (IllegalArgumentException e) {
            logger.warn("Last exercise not found for user: {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure finding last exercise", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findExerciseById/{id}")
    public ResponseEntity<?> findExerciseById(Authentication authentication, @PathVariable("id") Long userExerciseId) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to get exercise: {} for user: {}", userExerciseId, username);
        try {
            UserExercise userExercise = userExerciseService.findExerciseById(username, userExerciseId);
            return ResponseEntity.ok(mapToDTO(userExercise));
        }
        catch (IllegalArgumentException e){
            logger.error("Exercise lookup failed: {}: {}", userExerciseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure finding exercise by id", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExerciseById(Authentication authentication,
            @PathVariable("id") Long userExerciseId,
            @RequestBody UserExerciseUpdateRequest request) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to update exercise: {} for user: {}", userExerciseId, username);
        try {
            UserExercise updatedUserExercise = userExerciseService.updateUserExerciseById(username, userExerciseId,
                    request);
            return ResponseEntity.ok(mapToDTO(updatedUserExercise));
        }
        catch (IllegalArgumentException e){
            logger.error("Update failed for exercise: {}: {}", userExerciseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure updating exercise", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExercise(Authentication authentication, @PathVariable("id") Long userExerciseId) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        logger.info("REST request to delete exercise: {} for user: {}", userExerciseId, username);
        try {
            userExerciseService.deleteUserExerciseById(username, userExerciseId);
            return ResponseEntity.ok("User exercise deleted successfully");
        }
        catch (IllegalArgumentException e) {
            logger.error("Deletion failed for exercise: {}: {}", userExerciseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Error: Critical failure deleting exercise", e);
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
