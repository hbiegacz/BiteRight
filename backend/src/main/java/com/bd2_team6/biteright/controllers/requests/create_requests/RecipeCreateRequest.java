package com.bd2_team6.biteright.controllers.requests.create_requests;

import java.util.Set;

import com.bd2_team6.biteright.controllers.DTO.RecipeContentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RecipeCreateRequest {
    private String name;
    private String description;
    private String imageUrl;
    private Set<RecipeContentDTO> contents;
}
