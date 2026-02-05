package com.bd2_team6.biteright.weight_history;

import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.weight_history.WeightHistory;
import com.bd2_team6.biteright.entities.weight_history.WeightHistoryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WeightHistoryTests {

    @Autowired
    private WeightHistoryRepository weightHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveWeightHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WeightHistory weightHistory = new WeightHistory(user, LocalDateTime.parse("2025-05-12T00:00:00"), 70.5f);
        weightHistoryRepository.save(weightHistory);

        WeightHistory found = weightHistoryRepository.findById(weightHistory.getWeightId()).orElse(null);
        assertNotNull(found);
        assertEquals(70.5f, found.getWeight());
        assertEquals(user.getUsername(), found.getUser().getUsername());
    }

    @Test
    public void shouldUpdateWeightHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WeightHistory weightHistory = new WeightHistory(user, LocalDateTime.parse("2025-05-12T00:00:00"), 70.5f);
        weightHistoryRepository.save(weightHistory);

        weightHistory.setWeight(72.0f);
        weightHistoryRepository.save(weightHistory);

        WeightHistory updated = weightHistoryRepository.findById(weightHistory.getWeightId()).orElse(null);
        assertNotNull(updated);
        assertEquals(72.0f, updated.getWeight());
    }

    @Test
    public void shouldDeleteWeightHistory() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WeightHistory weightHistory = new WeightHistory(user, LocalDateTime.parse("2025-05-12T00:00:00"), 70.5f);
        weightHistoryRepository.save(weightHistory);

        weightHistoryRepository.delete(weightHistory);
        WeightHistory deleted = weightHistoryRepository.findById(weightHistory.getWeightId()).orElse(null);
        assertNull(deleted);
    }

    @Test
    public void shouldFindWeightHistoryByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WeightHistory wh1 = new WeightHistory(user, LocalDateTime.parse("2025-05-12T00:00:00"), 70.0f);
        WeightHistory wh2 = new WeightHistory(user, LocalDateTime.parse("2025-05-12T00:00:00"), 71.0f);
        weightHistoryRepository.save(wh1);
        weightHistoryRepository.save(wh2);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        Set<WeightHistory> found = refreshedUser.getWeightHistories();
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    @Test
    public void shouldDeleteWeightHistoryWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");
        WeightHistory weightHistory = new WeightHistory(user, LocalDateTime.parse("2025-05-12T00:00:00"), 70.0f);

        user.getWeightHistories().add(weightHistory);
        userRepository.save(user);

        Integer userId = user.getId();
        Integer weightId = weightHistory.getWeightId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(weightHistoryRepository.findById(weightId).isPresent());
    }

    @Test
    public void shouldNotSave2SameWeightHistoriesForUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        LocalDateTime sameDate = LocalDateTime.parse("2025-05-12T00:00:00");
        WeightHistory wh1 = new WeightHistory(user, sameDate, 70.0f);
        WeightHistory wh2 = new WeightHistory(user, sameDate, 70.0f);

        user.getWeightHistories().add(wh1);
        user.getWeightHistories().add(wh2);

        userRepository.save(user);

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, saved.getWeightHistories().size());
    }
}
