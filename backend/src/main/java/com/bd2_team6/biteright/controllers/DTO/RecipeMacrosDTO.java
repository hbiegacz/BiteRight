package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMacrosDTO {
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
}
