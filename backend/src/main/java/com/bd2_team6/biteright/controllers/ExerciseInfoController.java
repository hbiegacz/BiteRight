package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.ExerciseInfoDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.ExerciseInfoCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.ExerciseInfoUpdateRequest;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;
import com.bd2_team6.biteright.service.ExerciseInfoService;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exerciseInfo")
@RequiredArgsConstructor
public class ExerciseInfoController {
    private final ExerciseInfoService exerciseInfoService;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseInfoController.class);

    @GetMapping("/find/{name}")
    public ResponseEntity<?> findExerciseInfo(@PathVariable("name") String exerciseName) {
        try {
            Set<ExerciseInfoDTO> info = exerciseInfoService.findExerciseInfoByName(exerciseName);
            return ResponseEntity.ok(info);
        } catch (IllegalArgumentException e) {
            logger.error("Error finding exercise info.\n" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExerciseInfo(@RequestBody ExerciseInfoCreateRequest request){
        try {
            ExerciseInfo newInfo = exerciseInfoService.createExerciseInfo(request);
            return ResponseEntity.ok(newInfo);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating exercise info.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{name}") 
    public ResponseEntity<?> updateExerciseInfo(@PathVariable("name") String exerciseName, @RequestBody ExerciseInfoUpdateRequest request){
        try {
            ExerciseInfo updatedInfo = exerciseInfoService.updateExerciseInfo(exerciseName, request);
            return ResponseEntity.ok(updatedInfo);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating exercise info.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{name}") 
    public ResponseEntity<?> deleteExerciseInfo(@PathVariable("name") String exerciseName) {
        try {
            exerciseInfoService.deleteExerciseInfo(exerciseName);
            return ResponseEntity.ok("Exercise info delete successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting exercise info.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
