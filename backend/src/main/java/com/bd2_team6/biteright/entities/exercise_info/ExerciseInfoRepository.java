package com.bd2_team6.biteright.entities.exercise_info;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseInfoRepository extends JpaRepository<ExerciseInfo, Long> {
    Set<ExerciseInfo> findByNameContainingIgnoreCase(String name);
    Optional<ExerciseInfo> findByName(String name);
}
