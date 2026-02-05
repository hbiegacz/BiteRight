package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyLimitsDTO {
    private Long dailyLimitId;
    private Integer calorieLimit;
    private Integer proteinLimit;
    private Integer fatLimit;  
    private Integer carbLimit;  
    private Integer waterGoal;  
}