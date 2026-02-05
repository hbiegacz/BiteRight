package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.WaterIntakeDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.WaterIntakeCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.WaterIntakeUpdateRequest;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.water_intake.WaterIntake;
import com.bd2_team6.biteright.service.WaterIntakeService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/waterIntake")
@RequiredArgsConstructor
public class WaterIntakeController {
    private static final Logger logger = LoggerFactory.getLogger(WaterIntakeController.class);
    private final WaterIntakeService waterIntakeService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createWaterIntake(Authentication authentication,
                                                         @RequestBody WaterIntakeCreateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            WaterIntake waterIntake = waterIntakeService.createWaterIntake(username, request);
            return ResponseEntity.ok(mapToDTO(waterIntake));
        }
        catch (IllegalArgumentException e){
            logger.error("Error creating water intake." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating water intake." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findWaterIntakesForUser")
    public ResponseEntity<?> findWaterIntakeForUser(Authentication authentication,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "desc") String sortDir,
                                                    @RequestParam(defaultValue = "intakeDate") String sortBy) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<WaterIntake> waterIntakes = waterIntakeService.findWaterIntakesByUsername(username, pageable);
            return ResponseEntity.ok(mapToDTOPage(waterIntakes));
        }
        catch (IllegalArgumentException e){
            logger.error("Error finding water intakes for user." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding water intakes for user." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findWaterIntakesByDate/{date}")
    public ResponseEntity<?> findWaterIntakesByDate(Authentication authentication,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "desc") String sortDir,
                            @RequestParam(defaultValue = "intakeDate") String sortBy,
                            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<WaterIntake> waterIntakes = waterIntakeService.findWaterIntakesByDate(username, date, pageable);
            return ResponseEntity.ok(mapToDTOPage(waterIntakes));
        } catch (IllegalArgumentException e) {
            logger.error("Error finding water intakes by date." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding water intakes by date." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findLastWaterIntake")
    public ResponseEntity<?> findLastWaterIntakesByDate(Authentication authentication) {

        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            WaterIntake waterIntake = waterIntakeService.findLastWaterIntakeByUsername(username);
            return ResponseEntity.ok(mapToDTO(waterIntake));
        }
        catch (IllegalArgumentException e) {
            logger.error("Error finding last water intake." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding last water intake." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findWaterIntakeById/{id}")
    public ResponseEntity<?> findWaterIntakeById(Authentication authentication,
            @PathVariable("id") Long waterIntakeId) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            WaterIntake waterIntake = waterIntakeService.findWaterIntakeById(username, waterIntakeId);
            return ResponseEntity.ok(mapToDTO(waterIntake));
        }
        catch (IllegalArgumentException e){
            logger.error("Error finding water intake by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding water intake by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateWaterIntakeById(Authentication authentication,
            @PathVariable("id") Long waterIntakeId,
                                                   @RequestBody WaterIntakeUpdateRequest request) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            WaterIntake updatedWaterIntake = waterIntakeService.updateWaterIntakeById(username, waterIntakeId, request);
            return ResponseEntity.ok(mapToDTO(updatedWaterIntake));
        }
        catch (IllegalArgumentException e){
            logger.error("Error updating water intake by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating water intake by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteWaterIntake(Authentication authentication, @PathVariable("id") Long waterIntakeId) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            waterIntakeService.deleteWaterIntakeById(username, waterIntakeId);
            return ResponseEntity.ok("Water intake deleted successfully");
        }
        catch (IllegalArgumentException e) {
            logger.error("Error deleting water intake by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting water intake by id." + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private WaterIntakeDTO mapToDTO(WaterIntake waterIntake) {
        return new WaterIntakeDTO(waterIntake.getWaterIntakeId(), waterIntake.getIntakeDate(),
                waterIntake.getWaterAmount());
    }

    private Page<WaterIntakeDTO> mapToDTOPage(Page<WaterIntake> page) {
        return page.map(this::mapToDTO);
    }
}
