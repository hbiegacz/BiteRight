package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitHistoryDTO {
    private Long historyId;
    private LocalDate dateChanged;
    private Integer calorieLimit;
    private Integer proteinLimit;
    private Integer fatLimit;
    private Integer carbLimit;
    private Integer waterGoal;
}
