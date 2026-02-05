package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.create_requests.WeightHistoryCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.WeightHistoryUpdateRequest;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.weight_history.WeightHistory;
import com.bd2_team6.biteright.entities.weight_history.WeightHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
public class WeightHistoryService {

    private final UserRepository userRepository;
    private final WeightHistoryRepository weightHistoryRepository;

    public WeightHistory createWeightHistory(String username, WeightHistoryCreateRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WeightHistory weightHistory = new WeightHistory(user, request.getMeasurementDate(), request.getWeight());

        weightHistoryRepository.save(weightHistory);
        return weightHistory;
    }

    public Page<WeightHistory> findWeightHistoriesByUsername(String username, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User not found");
        }
        return weightHistoryRepository.findByUserUsername(username, pageable);
    }

    public Page<WeightHistory> findWeightHistoriesByDate(String username, LocalDate date, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User not found");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        return weightHistoryRepository.findByUserUsernameAndMeasurementDateBetween(username, startOfDay, endOfDay, pageable);
    }

    public WeightHistory findLastWeightHistoryByUsername(String username) {
        return weightHistoryRepository.findTopByUserUsernameOrderByMeasurementDateDesc(username)
                .orElseThrow(() -> new IllegalArgumentException("No weight history records found for user: " + username));
    }

    public WeightHistory findWeightHistoryById(String username, Long weightHistoryId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        WeightHistory weightHistory = weightHistoryRepository.findById(weightHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("Weight history with provided id not found"));

        if (weightHistory.getUser().getId().equals(userId)) {
            return weightHistory;
        }
        else {
            throw new IllegalArgumentException("Weight history with provided id does not belong to user");
        }
    }

    public WeightHistory updateWeightHistoryById(String username, Long weightHistoryId,
            WeightHistoryUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        WeightHistory weightHistory = weightHistoryRepository.findById(weightHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("Weight history with provided id not found"));

        if (weightHistory.getUser().getId().equals(userId)) {
            weightHistory.setMeasurementDate(request.getMeasurementDate());
            weightHistory.setWeight(request.getWeight());
            weightHistoryRepository.save(weightHistory);
            return weightHistory;
        }
        else {
            throw new IllegalArgumentException("Weight history with provided id does not belong to user");
        }
    }

    public void deleteWeightHistoryById(String username, Long weightHistoryId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        WeightHistory weightHistory = weightHistoryRepository.findById(weightHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("Weight history with provided id not found"));

        if (weightHistory.getUser().getId().equals(userId)) {
            weightHistoryRepository.delete(weightHistory);
        }
        else {
            throw new IllegalArgumentException("Weight history with provided id does not belong to user");
        }
    }

    public WeightHistoryService(UserRepository userRepository, WeightHistoryRepository weightHistoryRepository) {
        this.userRepository = userRepository;
        this.weightHistoryRepository = weightHistoryRepository;
    }
}
