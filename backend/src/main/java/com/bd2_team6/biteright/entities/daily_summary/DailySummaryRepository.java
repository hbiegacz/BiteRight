package com.bd2_team6.biteright.entities.daily_summary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, DailySummaryId> {
    Optional<DailySummary> findByUserIdAndSummaryDate(Long userId, LocalDate summaryDate);
    
    List<DailySummary> findByUserIdAndSummaryDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}