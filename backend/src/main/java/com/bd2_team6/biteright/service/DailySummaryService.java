package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.entities.daily_summary.DailySummary;
import com.bd2_team6.biteright.entities.daily_summary.DailySummaryRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailySummaryService {
    private final UserRepository userRepository;
    private final DailySummaryRepository dailySummaryRepository;

    @Autowired
    public DailySummaryService(UserRepository userRepository, DailySummaryRepository dailySummaryRepository) {
        this.userRepository = userRepository;
        this.dailySummaryRepository = dailySummaryRepository;
    }

    public DailySummary findDailySummaryByUsernameAndDate(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return dailySummaryRepository.findByUserIdAndSummaryDate(user.getId(), date)
                .orElseThrow(() -> new IllegalArgumentException("Summary not found for given date"));
    }

    public List<DailySummary> findDailySummariesByUsernameBetweenDates(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return dailySummaryRepository.findByUserIdAndSummaryDateBetween(user.getId(), startDate, endDate);
    }

    public int calculateStreak(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today;

        while (true) {
            if (dailySummaryRepository.findByUserIdAndSummaryDate(user.getId(), checkDate).isPresent()) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                if (checkDate.equals(today)) {
                    checkDate = checkDate.minusDays(1);
                    continue;
                }
                break;
            }
        }

        return streak;
    }

    public double calculateAverageDailyCalories(String username, int days) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<DailySummary> summaries = dailySummaryRepository.findByUserIdAndSummaryDateBetween(
                user.getId(), startDate, endDate);

        if (summaries.isEmpty()) {
            return 0.0;
        }

        double totalCalories = summaries.stream()
                .mapToDouble(DailySummary::getCalories)
                .sum();

        return totalCalories / summaries.size();
    }
}