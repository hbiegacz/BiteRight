package com.bd2_team6.biteright.entities.water_intake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {
    Page<WaterIntake> findByUserUsername(String username, Pageable pageable);
    Page<WaterIntake> findByUserUsernameAndIntakeDateBetween(
            String username, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Optional<WaterIntake> findTopByUserUsernameOrderByIntakeDateDesc(String username);
}
