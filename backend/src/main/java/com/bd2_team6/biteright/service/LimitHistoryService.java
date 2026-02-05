package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.entities.limit_history.LimitHistory;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LimitHistoryService {
    private final UserRepository userRepository;

    public Set<LimitHistory> findLimitHistoryByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getLimitHistories();
    }
}
