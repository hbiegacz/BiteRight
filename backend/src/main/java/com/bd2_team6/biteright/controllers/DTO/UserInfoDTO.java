package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String name;
    private String surname;
    private Integer age;
    private Float weight;
    private Integer height;
    private String lifestyle;
    private Float bmi;
}
