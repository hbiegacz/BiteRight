package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.create_requests.DailyLimitsCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.DailyLimitsUpdateRequest;
import com.bd2_team6.biteright.entities.daily_limits.DailyLimits;
import com.bd2_team6.biteright.entities.daily_limits.DailyLimitsRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyLimitsService {
    private final UserRepository userRepository;
    private final DailyLimitsRepository dailyLimitsRepository;

    @Autowired
    public DailyLimitsService(UserRepository userRepository, DailyLimitsRepository dailyLimitsRepository) {
        this.userRepository = userRepository;
        this.dailyLimitsRepository = dailyLimitsRepository;
    }

    public DailyLimits findDailyLimitsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getDailyLimits();
    }
    
    public DailyLimits createDailyLimits(String username, DailyLimitsCreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        DailyLimits newDailyLimits = new DailyLimits();
        newDailyLimits.setCalorieLimit(request.getCalorieLimit());
        newDailyLimits.setProteinLimit(request.getCalorieLimit());
        newDailyLimits.setFatLimit(request.getFatLimit());
        newDailyLimits.setCarbLimit(request.getCarbLimit());
        newDailyLimits.setWaterGoal(request.getWaterGoal());
        newDailyLimits.setUser(user);
        dailyLimitsRepository.save(newDailyLimits);

        return newDailyLimits;
    }

    public DailyLimits updateDailyLimits(String username, DailyLimitsUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        DailyLimits dailyLimits = user.getDailyLimits();
        dailyLimits.setCalorieLimit(request.getCalorieLimit());
        dailyLimits.setProteinLimit(request.getCalorieLimit());
        dailyLimits.setFatLimit(request.getFatLimit());
        dailyLimits.setCarbLimit(request.getCarbLimit());
        dailyLimits.setWaterGoal(request.getWaterGoal());
        dailyLimitsRepository.save(dailyLimits);

        return dailyLimits;
    }

    /**
     * Creates daily limits for a user object directly (used during registration)
     */
    public DailyLimits createDailyLimitsForUser(User user, Integer calorieLimit, Integer proteinLimit,
            Integer carbLimit, Integer fatLimit, Integer waterGoal) {
        DailyLimits newDailyLimits = new DailyLimits(user, calorieLimit, proteinLimit, fatLimit, carbLimit, waterGoal);
        return dailyLimitsRepository.save(newDailyLimits);
    }
}
