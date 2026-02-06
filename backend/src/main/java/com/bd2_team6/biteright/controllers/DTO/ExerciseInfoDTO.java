package com.bd2_team6.biteright.controllers.DTO;


import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseInfoDTO {
    private Long id;
    private Float metabolicEquivalent;
    private String name;

    public ExerciseInfoDTO(ExerciseInfo exerciseInfo) {
        this.id = exerciseInfo.getExerciseId();
        this.metabolicEquivalent = exerciseInfo.getMetabolicEquivalent();
        this.name = exerciseInfo.getName();
    } 
}
