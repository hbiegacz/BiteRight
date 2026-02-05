package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.controllers.DTO.LimitHistoryDTO;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bd2_team6.biteright.entities.limit_history.LimitHistory;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.service.LimitHistoryService;

@RestController
@RequestMapping("/limitHistory")
@RequiredArgsConstructor
public class LimitHistoryController {
    private final LimitHistoryService limitHistoryService;
    private final UserRepository userRepository;

    @GetMapping("/find")
    public ResponseEntity<?> findLimitHistory(Authentication authentication) {
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
        try {
            Set<LimitHistory> limitHistories = limitHistoryService.findLimitHistoryByUsername(username);
            return ResponseEntity.ok(mapToDTOSet(limitHistories));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private LimitHistoryDTO mapToDTO(LimitHistory limitHistory) {
        return new LimitHistoryDTO(limitHistory.getHistoryId(), limitHistory.getDateChanged(),
                limitHistory.getCalorieLimit(), limitHistory.getProteinLimit(), limitHistory.getFatLimit(),
                limitHistory.getCarbLimit(), limitHistory.getWaterGoal());
    }

    private Set<LimitHistoryDTO> mapToDTOSet(Set<LimitHistory> set) {
        return set.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toSet());
    }
}
