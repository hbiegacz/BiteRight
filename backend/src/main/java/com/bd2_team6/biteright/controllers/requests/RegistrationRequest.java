package com.bd2_team6.biteright.controllers.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    // Basic registration fields
    private String username;
    private String password;
    private String email;

    // Personal info (optional - from onboarding step 1)
    private String name;
    private String surname;
    private Integer age;

    // Body stats (optional - from onboarding step 2)
    private Float weight;
    private Integer height;
    private Float bmi;

    // Lifestyle (optional - from onboarding step 3)
    private String lifestyle;

    // Goals (optional - from onboarding step 4)
    private String goalType;
    private Float goalWeight;
    private String goalDate; // Will be parsed to LocalDate

    // Daily limits (optional - calculated on frontend)
    private Integer calorieLimit;
    private Integer proteinLimit;
    private Integer carbLimit;
    private Integer fatLimit;
    private Integer waterGoal;
}
