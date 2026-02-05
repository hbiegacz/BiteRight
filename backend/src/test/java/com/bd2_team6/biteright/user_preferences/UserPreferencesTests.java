package com.bd2_team6.biteright.user_preferences;

import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.user_preferences.UserPreferences;
import com.bd2_team6.biteright.entities.user_preferences.UserPreferencesRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserPreferencesTests {

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveUserPreferences() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        UserPreferences preferences = new UserPreferences(user, "PL", true, "Arial", true);
        userPreferencesRepository.save(preferences);

        UserPreferences found = userPreferencesRepository.findById(preferences.getUserPreferencesId()).orElse(null);
        assertNotNull(found);
        assertEquals("PL", found.getLanguage());
        assertTrue(found.getDarkmode());
        assertEquals("Arial", found.getFont());
        assertTrue(found.getNotifications());
        assertEquals(user.getUsername(), found.getUser().getUsername());
    }

    @Test
    public void shouldUpdateUserPreferences() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        UserPreferences preferences = new UserPreferences(user, "EN", false, "Arial", false);
        userPreferencesRepository.save(preferences);

        preferences.setLanguage("FR");
        preferences.setDarkmode(true);
        userPreferencesRepository.save(preferences);

        UserPreferences updated = userPreferencesRepository.findById(preferences.getUserPreferencesId()).orElse(null);
        assertNotNull(updated);
        assertEquals("FR", updated.getLanguage());
        assertTrue(updated.getDarkmode());
    }

    @Test
    public void shouldDeleteUserPreferences() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        UserPreferences preferences = new UserPreferences(user, "EN", false, "Arial", false);
        userPreferencesRepository.save(preferences);

        userPreferencesRepository.delete(preferences);

        UserPreferences deleted = userPreferencesRepository.findById(preferences.getUserPreferencesId()).orElse(null);
        assertNull(deleted);
    }

    @Test
    public void shouldFindUserPreferencesByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        UserPreferences preferences = new UserPreferences(user, "PL", true, "Arial", true);
        userPreferencesRepository.save(preferences);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        UserPreferences foundPreferences = refreshedUser.getUserPreferences();
        assertNotNull(foundPreferences);
        assertEquals(preferences.getUserPreferencesId(), foundPreferences.getUserPreferencesId());
    }

    @Test
    public void shouldDeleteUserPreferencesWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");
        UserPreferences preferences = new UserPreferences(user, "EN", false, "Arial", false);

        user.setUserPreferences(preferences);
        userRepository.save(user);

        Long userId = user.getId();
        Long preferencesId = preferences.getUserPreferencesId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(userPreferencesRepository.findById(preferencesId).isPresent());
    }
}
