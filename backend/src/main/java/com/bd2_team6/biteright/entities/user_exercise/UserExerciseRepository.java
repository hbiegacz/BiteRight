package com.bd2_team6.biteright.entities.user_exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    Page<UserExercise> findByUserUsername(String username, Pageable pageable);
    Page<UserExercise> findByUserUsernameAndActivityDateBetween(String username, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Optional<UserExercise> findTopByUserUsernameOrderByActivityDateDesc(String username);
}
