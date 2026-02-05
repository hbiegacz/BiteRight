package com.bd2_team6.biteright.controllers.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDTO {
    private Long id;
    private String language;
    private Boolean darkmode;
    private String font;
    private Boolean notifications;
}
