package com.bd2_team6.biteright.entities.daily_limits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DailyLimitsRepository extends JpaRepository<DailyLimits, Long> {
    
}
