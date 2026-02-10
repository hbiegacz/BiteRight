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
        newDailyLimits.setProteinLimit(request.getProteinLimit());
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
        dailyLimits.setProteinLimit(request.getProteinLimit());
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

    public void recalculateDailyLimits(User user) {
        if (user.getUserInfo() == null || user.getUserInfo().getUserGoal() == null) {
            return;
        }

        com.bd2_team6.biteright.entities.user_info.UserInfo info = user.getUserInfo();
        com.bd2_team6.biteright.entities.user_goal.UserGoal goal = info.getUserGoal();

        float weight = info.getWeight() != null ? info.getWeight() : 70f;
        int height = info.getHeight() != null ? info.getHeight() : 170;
        int age = info.getAge() != null ? info.getAge() : 25;
        String lifestyle = info.getLifestyle() != null ? info.getLifestyle().toLowerCase() : "moderate";
        String goalType = goal.getGoalType() != null ? goal.getGoalType().toLowerCase() : "maintain";

        // BMR (Mifflin-St Jeor) - using an average constant for gender-neutrality
        double bmr = (10 * weight) + (6.25 * height) - (5 * age) - 78;

        double activityFactor = switch (lifestyle) {
            case "sedentary" -> 1.2;
            case "light" -> 1.375;
            case "moderate" -> 1.55;
            case "active" -> 1.725;
            case "athlete" -> 1.9;
            default -> 1.55;
        };

        double tdee = bmr * activityFactor;

        // Goal adjustment
        int calories;
        if (goalType.contains("lose")) {
            calories = (int) (tdee - 500);
        } else if (goalType.contains("gain")) {
            calories = (int) (tdee + 500);
        } else {
            calories = (int) tdee;
        }

        // Ensure minimum calories
        calories = Math.max(calories, 1200);

        int protein = (int) (weight * 1.8);
        int fat = (int) ((calories * 0.25) / 9);
        int carbs = (int) ((calories - (protein * 4) - (fat * 9)) / 4);
        int waterGoal = (int) (weight * 35);

        DailyLimits limits = user.getDailyLimits();
        if (limits == null) {
            limits = new DailyLimits();
            limits.setUser(user);
        }

        limits.setCalorieLimit(calories);
        limits.setProteinLimit(protein);
        limits.setFatLimit(fat);
        limits.setCarbLimit(carbs);
        limits.setWaterGoal(waterGoal);

        dailyLimitsRepository.save(limits);
    }
}
