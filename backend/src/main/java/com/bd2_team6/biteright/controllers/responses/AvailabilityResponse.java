package com.bd2_team6.biteright.controllers.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityResponse {
    private boolean usernameAvailable;
    private boolean emailAvailable;
    private String message;
}
