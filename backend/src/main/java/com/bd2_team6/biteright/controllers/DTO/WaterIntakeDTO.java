package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterIntakeDTO {
    private Long id;
    private LocalDateTime intakeDate;
    private Integer waterAmount;
}