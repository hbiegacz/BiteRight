package com.bd2_team6.biteright.controllers;

import com.bd2_team6.biteright.entities.daily_summary.DailySummary;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.service.DailySummaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dailySummary")
@RequiredArgsConstructor
public class DailySummaryController {
    private static final Logger logger = LoggerFactory.getLogger(DailySummaryController.class);

    private final DailySummaryService dailySummaryService;
    private final UserRepository userRepository;

    @GetMapping("/find")
    public ResponseEntity<?> findDailySummary(
            Authentication authentication,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);

        try {
            if (date != null) {
                // Single date
                LocalDate summaryDate = LocalDate.parse(date);
                return dailySummaryService.findDailySummaryByUsernameAndDate(username, summaryDate)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.ok(new DailySummary())); // Return empty object which maps to zeros/nulls
            } else if (startDate != null && endDate != null) {
                // Date range
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                List<DailySummary> summaries = dailySummaryService.findDailySummariesByUsernameBetweenDates(username, start, end);
                return ResponseEntity.ok(summaries);
            } else {
                // Default to today's date
                return dailySummaryService.findDailySummaryByUsernameAndDate(username, LocalDate.now())
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.ok(new DailySummary()));
            }
        } catch (Exception e) {
            logger.error("Error finding daily summary.\n" + e.getMessage());
            return ResponseEntity.badRequest().body("Error parsing dates or finding summary: " + e.getMessage());
        }
    }

    @GetMapping("/streak")
    public ResponseEntity<?> getStreak(Authentication authentication) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            int streak = dailySummaryService.calculateStreak(username);
            return ResponseEntity.ok(streak);
        } catch (IllegalArgumentException e) {
            logger.error("Error calculating streak.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/averageCalories")
    public ResponseEntity<?> getAverageCalories(
            Authentication authentication,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            double avgCalories = dailySummaryService.calculateAverageDailyCalories(username, start, end);
            return ResponseEntity.ok(avgCalories);
        } catch (Exception e) {
            logger.error("Error calculating average calories.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/averageProtein")
    public ResponseEntity<?> getAverageProtein(
            Authentication authentication,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            String username = ControllerHelperClass.getUsernameFromAuthentication(authentication, userRepository);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            double avgProtein = dailySummaryService.calculateAverageDailyProtein(username, start, end);
            return ResponseEntity.ok(avgProtein);
        } catch (Exception e) {
            logger.error("Error calculating average protein.\n" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}