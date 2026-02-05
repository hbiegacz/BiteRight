package com.bd2_team6.biteright.controllers.requests.update_requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class MealContentUpdateRequest {
    private Long ingredientId;
    private Long mealId;
    private Integer ingredientAmount;
}
