package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.create_requests.MealInfoCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.MealInfoUpdateRequest;
import com.bd2_team6.biteright.entities.meal_info.MealInfo;
import com.bd2_team6.biteright.entities.meal_info.MealInfoRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealInfoService {
    private final MealInfoRepository mealInfoRepository;
    private final UserRepository userRepository;

    @Autowired
    public MealInfoService(MealInfoRepository mealInfoRepository, UserRepository userRepository) {
        this.mealInfoRepository = mealInfoRepository;
        this.userRepository = userRepository;
    }

    public MealInfo findMealInfoById(Long mealId) {
        MealInfo mealInfo = mealInfoRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal info not found"));
        return mealInfo;
    }

    public MealInfo findMealInfoByName(String mealName) {
        MealInfo mealInfo = mealInfoRepository.findByMealName(mealName)
                .orElseThrow(() -> new IllegalArgumentException("Meal info not found"));
        return mealInfo;
    }

    public MealInfo createMealInfo(String username, MealInfoCreateRequest request) {  
        User user = userRepository.findByUsername(username)     
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        MealInfo newMealInfo = new MealInfo();
        newMealInfo.setCalories(request.getCalories());
        newMealInfo.setCarbs(request.getCarbs());
        newMealInfo.setFat(request.getCarbs());
        newMealInfo.setUserId(user.getId());
        newMealInfo.setMealName(request.getMealName());
        newMealInfo.setProtein(request.getProtein());

        return mealInfoRepository.save(newMealInfo);
    }

    public MealInfo updateMealInfo(Long mealId, MealInfoUpdateRequest request) {
        MealInfo newMealInfo = mealInfoRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal info not found"));

        newMealInfo.setCalories(request.getCalories());
        newMealInfo.setCarbs(request.getCarbs());
        newMealInfo.setFat(request.getCarbs());
        newMealInfo.setMealName(request.getMealName());
        newMealInfo.setProtein(request.getProtein());

        return mealInfoRepository.save(newMealInfo);
    }

    public void deleteMealInfo(Long mealId) {
        MealInfo mealInfo = mealInfoRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal info not found"));

        mealInfoRepository.delete(mealInfo);
    }
}
