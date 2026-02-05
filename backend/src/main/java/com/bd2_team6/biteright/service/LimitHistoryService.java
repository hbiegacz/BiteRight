package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.entities.limit_history.LimitHistory;
import com.bd2_team6.biteright.entities.limit_history.LimitHistoryRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LimitHistoryService {
    private final UserRepository userRepository;
    private final LimitHistoryRepository limitHistoryRepository; // TODO: remove this

    @Autowired
    public LimitHistoryService(UserRepository userRepository, LimitHistoryRepository limitHistoryRepository) {
        this.userRepository = userRepository;
        this.limitHistoryRepository = limitHistoryRepository;
    }

    public Set<LimitHistory> findLimitHistoryByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getLimitHistories();
    }
}
