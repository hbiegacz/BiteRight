package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.create_requests.UserExerciseCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.UserExerciseUpdateRequest;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfoRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bd2_team6.biteright.entities.user_exercise.UserExercise;
import com.bd2_team6.biteright.entities.user_exercise.UserExerciseRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserExerciseService {

    private final UserRepository userRepository;
    private final ExerciseInfoRepository exerciseInfoRepository;
    private final UserExerciseRepository userExerciseRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserExerciseService.class);

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public UserExercise createUserExercise(String username, UserExerciseCreateRequest request) {
        logger.info("Creating exercise for user: {}, exerciseInfoId: {}", username, request.getExerciseInfoId());
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.error("User not found: {}", username);
            return new IllegalArgumentException("User not found");
        });
        ExerciseInfo exerciseInfo = exerciseInfoRepository.findById(request.getExerciseInfoId()).orElseThrow(() -> {
            logger.error("Exercise info not found: {}", request.getExerciseInfoId());
            return new IllegalArgumentException("Exercise info not found");
        });
        UserExercise userExercise = new UserExercise(user, exerciseInfo, request.getActivityDate(), request.getDuration());
        UserExercise savedUserExercise = userExerciseRepository.save(userExercise);

        userExerciseRepository.flush();
        entityManager.refresh(savedUserExercise);

        logger.info("Successfully created exercise: {} for user: {}", savedUserExercise.getUserExerciseId(), username);
        return savedUserExercise;
    }

    public Page<UserExercise> findUserExercisesByUsername(String username, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            logger.error("User not found for search: {}", username);
            throw new IllegalArgumentException("User not found");
        }
        return userExerciseRepository.findByUserUsername(username, pageable);
    }

    public Page<UserExercise> findUserExercisesByDate(String username, LocalDate date, Pageable pageable) {
        if (!userRepository.existsByUsername(username)) {
            logger.error("User not found for date search: {}", username);
            throw new IllegalArgumentException("User not found");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        return userExerciseRepository.findByUserUsernameAndActivityDateBetween(username, startOfDay, endOfDay, pageable);
    }

    public UserExercise findLastExerciseByUsername(String username) {
        return userExerciseRepository.findTopByUserUsernameOrderByActivityDateDesc(username)
                .orElseThrow(() -> {
                    logger.warn("No exercise history for user: {}", username);
                    return new IllegalArgumentException("No user's exercise records found for user: " + username);
                });
    }


    public UserExercise findExerciseById(String username, Long userExerciseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("User's exercise with provided id not found"));

        if (userExercise.getUser().getId().equals(userId)) {
            return userExercise;
        }
        else {
            throw new IllegalArgumentException("User's exercise with provided id does not belong to user");
        }
    }

    public UserExercise updateUserExerciseById(String username, Long userExerciseId,
                                                    UserExerciseUpdateRequest request) {
        logger.info("Updating exercise: {} for user: {}", userExerciseId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found for update: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        Long userId = user.getId();
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> {
                    logger.error("Exercise not found: {}", userExerciseId);
                    return new IllegalArgumentException("User's exercise with provided id not found");
                });

        if (userExercise.getUser().getId().equals(userId)) {
            if (request.getExerciseInfoId() != null) {
                ExerciseInfo newInfo = exerciseInfoRepository.findById(request.getExerciseInfoId())
                        .orElseThrow(() -> new IllegalArgumentException("New exercise info not found"));
                userExercise.setExerciseInfo(newInfo);
            }
            userExercise.setActivityDate(request.getActivityDate());
            userExercise.setDuration(request.getDuration());
            UserExercise updated = userExerciseRepository.save(userExercise);
            logger.info("Successfully updated exercise: {}", userExerciseId);
            return updated;
        }
        else {
            logger.error("User: {} attempted to update unauthorized exercise: {}", username, userExerciseId);
            throw new IllegalArgumentException("User's exercise with provided id does not belong to user");
        }
    }

    public void deleteUserExerciseById(String username, Long userExerciseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        UserExercise userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("User's exercise with provided id not found"));

        if (userExercise.getUser().getId().equals(userId)) {
            userExerciseRepository.delete(userExercise);
        }
        else {
            throw new IllegalArgumentException("User's exercise with provided id does not belong to user");
        }
    }

    public UserExerciseService(UserRepository userRepository, ExerciseInfoRepository exerciseInfoRepository, UserExerciseRepository userExerciseRepository) {
        this.userRepository = userRepository;
        this.exerciseInfoRepository = exerciseInfoRepository;
        this.userExerciseRepository = userExerciseRepository;
    }
}
