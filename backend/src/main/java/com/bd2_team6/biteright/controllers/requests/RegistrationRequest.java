package com.bd2_team6.biteright.controllers.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;

    private String name;
    private String surname;
    private Integer age;

    private Float weight;
    private Integer height;
    private Float bmi;
    private String lifestyle;

    private String goalType;
    private Float goalWeight;
    private String goalDate;

    private Integer calorieLimit;
    private Integer proteinLimit;
    private Integer carbLimit;
    private Integer fatLimit;
    private Integer waterGoal;
}
