package com.bd2_team6.biteright.controllers.requests.update_requests;

import java.util.List;

import com.bd2_team6.biteright.controllers.DTO.MealContentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class MealUpdateRequest {
    private Long mealTypeId;
    private String name;
    private String description;
    private List<MealContentDTO> contents;
}
