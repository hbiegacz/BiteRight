package com.bd2_team6.biteright.entities.limit_history;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LimitHistoryRepository extends JpaRepository<LimitHistory, Long> {

}
