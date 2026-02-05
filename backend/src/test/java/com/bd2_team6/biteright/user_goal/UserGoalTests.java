package com.bd2_team6.biteright.user_goal;

import com.bd2_team6.biteright.entities.user_goal.UserGoal;
import com.bd2_team6.biteright.entities.user_goal.UserGoalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserGoalTests {

    @Autowired
    private UserGoalRepository userGoalRepository;

    @Test
    public void shouldSaveUserGoal() {
        UserGoal goal = new UserGoal("Lose Weight", 70.0f, LocalDate.parse("2025-05-12"));
        userGoalRepository.save(goal);

        UserGoal saved = userGoalRepository.findById(goal.getUserGoalId()).orElse(null);
        assertNotNull(saved);
        assertEquals("Lose Weight", saved.getGoalType());
        assertEquals(70.0f, saved.getGoalWeight());
        assertEquals(LocalDate.parse("2025-05-12"), saved.getDeadline());
    }

    @Test
    public void shouldUpdateUserGoal() {
        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        goal.setGoalType("Maintain Weight");
        goal.setGoalWeight(75.0f);
        goal.setDeadline(LocalDate.parse("2025-05-12"));
        userGoalRepository.save(goal);

        UserGoal updated = userGoalRepository.findById(goal.getUserGoalId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Maintain Weight", updated.getGoalType());
        assertEquals(75.0f, updated.getGoalWeight());
        assertEquals(LocalDate.parse("2025-05-12"), updated.getDeadline());
    }

    @Test
    public void shouldDeleteUserGoal() {
        UserGoal goal = new UserGoal("Gain Strength", 85.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        userGoalRepository.delete(goal);

        UserGoal deleted = userGoalRepository.findById(goal.getUserGoalId()).orElse(null);
        assertNull(deleted);
    }
}
