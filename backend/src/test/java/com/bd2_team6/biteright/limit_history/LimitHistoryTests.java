package com.bd2_team6.biteright.limit_history;

import com.bd2_team6.biteright.entities.limit_history.LimitHistory;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.limit_history.LimitHistoryRepository;
import com.bd2_team6.biteright.entities.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LimitHistoryTests {

    @Autowired
    private LimitHistoryRepository limitHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveLimitHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        LimitHistory limitHistory = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);

        limitHistoryRepository.save(limitHistory);

        LimitHistory foundLimitHistory = limitHistoryRepository.findById(limitHistory.getHistoryId()).orElse(null);
        assertNotNull(foundLimitHistory);
        assertEquals(limitHistory.getCalorieLimit(), foundLimitHistory.getCalorieLimit());
        assertEquals(limitHistory.getProteinLimit(), foundLimitHistory.getProteinLimit());
        assertEquals(limitHistory.getFatLimit(), foundLimitHistory.getFatLimit());
        assertEquals(limitHistory.getWaterGoal(), foundLimitHistory.getWaterGoal());
        assertEquals(limitHistory.getUser().getUsername(), foundLimitHistory.getUser().getUsername());
    }

    @Test
    public void shouldUpdateLimitHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        LimitHistory limitHistory = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);
        limitHistoryRepository.save(limitHistory);

        limitHistory.setCalorieLimit(2800);
        limitHistory.setWaterGoal(4);
        limitHistoryRepository.save(limitHistory);

        LimitHistory updatedLimitHistory = limitHistoryRepository.findById(limitHistory.getHistoryId()).orElse(null);
        assertNotNull(updatedLimitHistory);
        assertEquals(2800, updatedLimitHistory.getCalorieLimit());
        assertEquals(4, updatedLimitHistory.getWaterGoal());
    }

    @Test
    public void shouldDeleteLimitHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        LimitHistory limitHistory = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);
        limitHistoryRepository.save(limitHistory);

        limitHistoryRepository.delete(limitHistory);

        LimitHistory deletedLimitHistory = limitHistoryRepository.findById(limitHistory.getHistoryId()).orElse(null);
        assertNull(deletedLimitHistory);
    }

    @Test
    public void shouldNotSaveDuplicateLimitHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        LimitHistory limitHistory1 = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);
        LimitHistory limitHistory2 = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);

        user.getLimitHistories().add(limitHistory1);
        user.getLimitHistories().add(limitHistory2);

        userRepository.save(user);

        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, savedUser.getLimitHistories().size());
    }

    @Test
    public void shouldDeleteLimitHistoryWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");

        LimitHistory limitHistory1 = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);
        LimitHistory limitHistory2 = new LimitHistory(LocalDate.parse("2025-05-12"), user, 2200, 130, 50, 3);

        user.getLimitHistories().add(limitHistory1);
        user.getLimitHistories().add(limitHistory2);

        userRepository.save(user);

        Long userId = user.getId();
        Long history1Id = limitHistory1.getHistoryId();
        Long history2Id = limitHistory2.getHistoryId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(limitHistoryRepository.findById(history1Id).isPresent());
        assertFalse(limitHistoryRepository.findById(history2Id).isPresent());
    }

    @Test
    public void shouldFindLimitHistoryByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        LimitHistory limitHistory1 = new LimitHistory(LocalDate.parse("2025-05-11"), user, 2500, 150, 60, 3);
        LimitHistory limitHistory2 = new LimitHistory(LocalDate.parse("2025-05-12"), user, 2200, 130, 50, 3);

        limitHistoryRepository.save(limitHistory1);
        limitHistoryRepository.save(limitHistory2);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        Set<LimitHistory> limitHistories = refreshedUser.getLimitHistories();

        assertEquals(2, limitHistories.size());

        assertTrue(limitHistories.stream().anyMatch(lh -> lh.getDateChanged().equals(LocalDate.parse("2025-05-11"))));
        assertTrue(limitHistories.stream().anyMatch(lh -> lh.getDateChanged().equals(LocalDate.parse("2025-05-12"))));
    }
}
