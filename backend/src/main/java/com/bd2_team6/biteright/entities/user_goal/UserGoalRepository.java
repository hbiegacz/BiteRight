package com.bd2_team6.biteright.entities.user_goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Integer> {
    
}
