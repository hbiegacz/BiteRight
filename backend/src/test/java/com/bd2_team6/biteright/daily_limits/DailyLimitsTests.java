package com.bd2_team6.biteright.daily_limits;

import com.bd2_team6.biteright.entities.daily_limits.DailyLimits;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.daily_limits.DailyLimitsRepository;
import com.bd2_team6.biteright.entities.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DailyLimitsTests {

    @Autowired
    private DailyLimitsRepository dailyLimitsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveDailyLimits() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        DailyLimits dailyLimits = new DailyLimits(user, 2000, 150, 70, 300, 2);
        dailyLimitsRepository.save(dailyLimits);

        DailyLimits foundDailyLimits = dailyLimitsRepository.findById(dailyLimits.getDailyLimitId()).orElse(null);
        assertNotNull(foundDailyLimits);
        assertEquals(dailyLimits.getCalorieLimit(), foundDailyLimits.getCalorieLimit());
        assertEquals(dailyLimits.getProteinLimit(), foundDailyLimits.getProteinLimit());
        assertEquals(dailyLimits.getFatLimit(), foundDailyLimits.getFatLimit());
        assertEquals(dailyLimits.getCarbLimit(), foundDailyLimits.getCarbLimit());
        assertEquals(dailyLimits.getWaterGoal(), foundDailyLimits.getWaterGoal());
        assertEquals(dailyLimits.getUser().getUsername(), foundDailyLimits.getUser().getUsername());
    }

    @Test
    public void shouldUpdateDailyLimits() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        DailyLimits dailyLimits = new DailyLimits(user, 2000, 150, 70, 300, 2);
        dailyLimitsRepository.save(dailyLimits);

        dailyLimits.setCalorieLimit(2500);
        dailyLimitsRepository.save(dailyLimits);

        DailyLimits updatedDailyLimits = dailyLimitsRepository.findById(dailyLimits.getDailyLimitId()).orElse(null);
        assertNotNull(updatedDailyLimits);
        assertEquals(2500, updatedDailyLimits.getCalorieLimit());
    }

    @Test
    public void shouldDeleteDailyLimits() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        DailyLimits dailyLimits = new DailyLimits(user, 2000, 150, 70, 300, 2);
        dailyLimitsRepository.save(dailyLimits);

        dailyLimitsRepository.delete(dailyLimits);

        DailyLimits deletedDailyLimits = dailyLimitsRepository.findById(dailyLimits.getDailyLimitId()).orElse(null);
        assertNull(deletedDailyLimits);
    }

    @Test
    public void shouldFindDailyLimitsByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        DailyLimits dailyLimits = new DailyLimits(user, 2000, 150, 70, 300, 2);
        dailyLimitsRepository.save(dailyLimits);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        DailyLimits foundDailyLimits = refreshedUser.getDailyLimits();
        assertNotNull(foundDailyLimits);
        assertEquals(dailyLimits.getDailyLimitId(), foundDailyLimits.getDailyLimitId());
        assertEquals(dailyLimits.getCalorieLimit(), foundDailyLimits.getCalorieLimit());
        assertEquals(dailyLimits.getProteinLimit(), foundDailyLimits.getProteinLimit());
        assertEquals(dailyLimits.getFatLimit(), foundDailyLimits.getFatLimit());
        assertEquals(dailyLimits.getCarbLimit(), foundDailyLimits.getCarbLimit());
        assertEquals(dailyLimits.getWaterGoal(), foundDailyLimits.getWaterGoal());
        assertEquals(dailyLimits.getUser().getUsername(), foundDailyLimits.getUser().getUsername());
    }

    @Test
    public void shouldDeleteDailyLimitsWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");
        DailyLimits dailyLimits = new DailyLimits(user, 2000, 150, 70, 300, 2);

        user.setDailyLimits(dailyLimits);
        userRepository.save(user);

        Long userId = user.getId();
        Long dailyLimitsId = dailyLimits.getDailyLimitId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(dailyLimitsRepository.findById(dailyLimitsId).isPresent());
    }
}
