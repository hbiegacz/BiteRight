package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.update_requests.UserGoalUpdateRequest;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.user_goal.UserGoal;
import com.bd2_team6.biteright.entities.user_goal.UserGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserGoalService {

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final DailyLimitsService dailyLimitsService;

    @Autowired
    public UserGoalService(UserRepository userRepository, UserGoalRepository userGoalRepository,
            DailyLimitsService dailyLimitsService) {
        this.userRepository = userRepository;
        this.userGoalRepository = userGoalRepository;
        this.dailyLimitsService = dailyLimitsService;
    }

    public UserGoal findUserGoalByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getUserInfo().getUserGoal();
    }

    public UserGoal updateUserGoal(String username, UserGoalUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserGoal userGoal = user.getUserInfo().getUserGoal();
        userGoal.setGoalType(request.getGoalType());
        userGoal.setGoalWeight(request.getGoalWeight());
        userGoal.setDeadline(request.getGoalDate());
        userGoalRepository.save(userGoal);

        dailyLimitsService.recalculateDailyLimits(user);

        return userGoal;
    }
}
