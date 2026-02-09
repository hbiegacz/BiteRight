package com.bd2_team6.biteright.controllers.requests.update_requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExerciseUpdateRequest {
    private Long exerciseInfoId;
    private LocalDateTime activityDate;
    private int duration;
}