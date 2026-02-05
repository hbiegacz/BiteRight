package com.bd2_team6.biteright.water_intake;

import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.water_intake.WaterIntake;
import com.bd2_team6.biteright.entities.water_intake.WaterIntakeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WaterIntakeTests {

    @Autowired
    private WaterIntakeRepository waterIntakeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveWaterIntake() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WaterIntake waterIntake = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);
        waterIntakeRepository.save(waterIntake);

        WaterIntake found = waterIntakeRepository.findById(waterIntake.getWaterIntakeId()).orElse(null);
        assertNotNull(found);
        assertEquals(500, found.getWaterAmount());
        assertEquals(user.getUsername(), found.getUser().getUsername());
    }

    @Test
    public void shouldUpdateWaterIntake() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WaterIntake waterIntake = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);
        waterIntakeRepository.save(waterIntake);

        waterIntake.setWaterAmount(750);
        waterIntakeRepository.save(waterIntake);

        WaterIntake updated = waterIntakeRepository.findById(waterIntake.getWaterIntakeId()).orElse(null);
        assertNotNull(updated);
        assertEquals(750, updated.getWaterAmount());
    }

    @Test
    public void shouldDeleteWaterIntake() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WaterIntake waterIntake = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);
        waterIntakeRepository.save(waterIntake);

        waterIntakeRepository.delete(waterIntake);
        WaterIntake deleted = waterIntakeRepository.findById(waterIntake.getWaterIntakeId()).orElse(null);

        assertNull(deleted);
    }

    @Test
    public void shouldFindWaterIntakeByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        WaterIntake waterIntake = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);
        WaterIntake waterIntake2 = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 510);
        waterIntakeRepository.save(waterIntake);
        waterIntakeRepository.save(waterIntake2);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        Set<WaterIntake> found = refreshedUser.getWaterIntakes();
        assertNotNull(found);
        assertEquals(2, refreshedUser.getWaterIntakes().size());
    }

    @Test
    public void shouldDeleteWaterIntakeWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");
        WaterIntake waterIntake = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);

        user.getWaterIntakes().add(waterIntake);
        userRepository.save(user);

        Long userId = user.getId();
        Long waterIntakeId = waterIntake.getWaterIntakeId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(waterIntakeRepository.findById(waterIntakeId).isPresent());
    }

    @Test
    public void shouldNotSave2SameWaterIntakesForUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        WaterIntake waterIntake1 = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);
        WaterIntake waterIntake2 = new WaterIntake(LocalDateTime.parse("2025-05-12T00:00:00"), user, 500);

        user.getWaterIntakes().add(waterIntake1);
        user.getWaterIntakes().add(waterIntake2);

        userRepository.save(user);

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, saved.getWaterIntakes().size());
    }
}
