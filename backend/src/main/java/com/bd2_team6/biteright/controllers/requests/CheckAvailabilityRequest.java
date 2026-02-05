package com.bd2_team6.biteright.controllers.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckAvailabilityRequest {
    private String username;
    private String email;
}
