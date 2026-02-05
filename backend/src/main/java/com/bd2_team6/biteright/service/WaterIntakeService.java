package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.create_requests.WaterIntakeCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.WaterIntakeUpdateRequest;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.water_intake.WaterIntake;
import com.bd2_team6.biteright.entities.water_intake.WaterIntakeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WaterIntakeService {

    private final UserRepository userRepository;
    private final WaterIntakeRepository waterIntakeRepository;

    public WaterIntake createWaterIntake(String username, WaterIntakeCreateRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WaterIntake waterIntake = new WaterIntake(request.getIntakeDate(), user, request.getWaterAmount());

        waterIntakeRepository.save(waterIntake);
        return waterIntake;
    }

    public Page<WaterIntake> findWaterIntakesByUsername(String username, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User not found");
        }
        return waterIntakeRepository.findByUserUsername(username, pageable);
    }

    public Page<WaterIntake> findWaterIntakesByDate(String username, LocalDate date, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User not found");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        return waterIntakeRepository.findByUserUsernameAndIntakeDateBetween(username, startOfDay, endOfDay, pageable);
    }

    public WaterIntake findLastWaterIntakeByUsername(String username) {
        return waterIntakeRepository.findTopByUserUsernameOrderByIntakeDateDesc(username)
                .orElseThrow(() -> new IllegalArgumentException("No water intake records found for user: " + username));
    }


    public WaterIntake findWaterIntakeById(String username, int waterIntakeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Integer userId = user.getId();
        WaterIntake waterIntake = waterIntakeRepository.findById(waterIntakeId)
                .orElseThrow(() -> new IllegalArgumentException("Water intake with provided id not found"));

        if (waterIntake.getUser().getId().equals(userId)) {
            return waterIntake;
        }
        else {
            throw new IllegalArgumentException("Water intake with provided id does not belong to user");
        }
    }

    public WaterIntake updateWaterIntakeById(String username, int waterIntakeId,
                                             WaterIntakeUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Integer userId = user.getId();
        WaterIntake waterIntake = waterIntakeRepository.findById(waterIntakeId)
                .orElseThrow(() -> new IllegalArgumentException("Water intake with provided id not found"));

        if (waterIntake.getUser().getId().equals(userId)) {
            waterIntake.setWaterAmount(request.getWaterAmount());
            waterIntakeRepository.save(waterIntake);
            return waterIntake;
        }
        else {
            throw new IllegalArgumentException("Water intake with provided id does not belong to user");
        }
    }

    public void deleteWaterIntakeById(String username, int waterIntakeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Integer userId = user.getId();
        WaterIntake waterIntake = waterIntakeRepository.findById(waterIntakeId)
                .orElseThrow(() -> new IllegalArgumentException("Water intake with provided id not found"));

        if (waterIntake.getUser().getId().equals(userId)) {
            waterIntakeRepository.delete(waterIntake);
        }
        else {
            throw new IllegalArgumentException("Water intake with provided id does not belong to user");
        }
    }

    public WaterIntakeService(UserRepository userRepository, WaterIntakeRepository waterIntakeRepository) {
        this.userRepository = userRepository;
        this.waterIntakeRepository = waterIntakeRepository;
    }
}
