package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String type;
    private Boolean isVerified;
    private String forgottenPasswordCode;
}
