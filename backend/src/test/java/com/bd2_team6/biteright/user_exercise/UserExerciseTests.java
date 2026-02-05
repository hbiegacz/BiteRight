package com.bd2_team6.biteright.user_exercise;

import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user_exercise.UserExercise;
import com.bd2_team6.biteright.entities.user_exercise.UserExerciseRepository;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserExerciseTests {

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseInfoRepository exerciseInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveUserExercise() {
        User user = new User("test_user", "test@example.com", "hash", "standard");
        userRepository.save(user);

        ExerciseInfo exercise = new ExerciseInfo(0.8f, "Running");
        exerciseInfoRepository.save(exercise);

        UserExercise userExercise = new UserExercise(user, exercise, LocalDateTime.parse("2025-05-11T00:00:00"), 30, 300);
        userExerciseRepository.save(userExercise);

        UserExercise found = userExerciseRepository.findById(userExercise.getUserExerciseId()).orElse(null);
        assertNotNull(found);
        assertEquals(30, found.getDuration());
        assertEquals(300, found.getCaloriesBurnt());
        assertEquals("Running", found.getExerciseInfo().getName());
    }

    @Test
    public void shouldUpdateUserExercise() {
        User user = new User("john", "john@example.com", "hash", "standard");
        userRepository.save(user);

        ExerciseInfo exercise = new ExerciseInfo(0.8f, "Light cardio");
        exerciseInfoRepository.save(exercise);

        UserExercise userExercise = new UserExercise(user, exercise, LocalDateTime.parse("2025-05-11T00:00:00"), 20, 100);
        userExerciseRepository.save(userExercise);

        userExercise.setDuration(25);
        userExercise.setCaloriesBurnt(150);
        userExerciseRepository.save(userExercise);

        UserExercise updated = userExerciseRepository.findById(userExercise.getUserExerciseId()).orElse(null);
        assertNotNull(updated);
        assertEquals(25, updated.getDuration());
        assertEquals(150, updated.getCaloriesBurnt());
    }

    @Test
    public void shouldDeleteUserExercise() {
        User user = new User("jane", "jane@example.com", "hash", "standard");
        userRepository.save(user);

        ExerciseInfo exercise = new ExerciseInfo(0.8f, "Outdoor activity");
        exerciseInfoRepository.save(exercise);

        UserExercise userExercise = new UserExercise(user, exercise, LocalDateTime.parse("2025-05-11T00:00:00"), 45, 400);
        userExerciseRepository.save(userExercise);

        userExerciseRepository.delete(userExercise);
        assertFalse(userExerciseRepository.findById(userExercise.getUserExerciseId()).isPresent());
    }

    @Test
    public void shouldFindExercisesByUser() {
        User user = new User("mike", "mike@example.com", "hash", "standard");
        userRepository.save(user);

        ExerciseInfo exerciseInfo1 = new ExerciseInfo(0.8f, "Water activity");
        ExerciseInfo exerciseInfo2 = new ExerciseInfo(0.8f, "Endurance");
        exerciseInfoRepository.saveAll(List.of(exerciseInfo1, exerciseInfo2));

        UserExercise userExercise1 = new UserExercise(user, exerciseInfo1, LocalDateTime.parse("2025-05-11T00:00:00"), 60, 500);
        UserExercise userExercise2 = new UserExercise(user, exerciseInfo2, LocalDateTime.parse("2025-05-12T00:00:00"), 40, 350);
        userExerciseRepository.saveAll(List.of(userExercise1, userExercise2));

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        Set<UserExercise> exercises = refreshedUser.getUserExercises();
        assertEquals(2, exercises.size());
    }

    @Test
    public void shouldFindExercisesByExerciseInfo() {
        User user = new User("emma", "emma@example.com", "hash", "standard");
        userRepository.save(user);

        ExerciseInfo exerciseInfo = new ExerciseInfo(0.2f, "Flexibility");
        exerciseInfoRepository.save(exerciseInfo);

        UserExercise userExercise1 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 30, 120);
        UserExercise userExercise2 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-12T00:00:00"), 45, 200);
        userExerciseRepository.saveAll(List.of(userExercise1, userExercise2));

        entityManager.flush();
        entityManager.clear();

        ExerciseInfo refreshed = exerciseInfoRepository.findById(exerciseInfo.getExerciseId()).orElse(null);
        assertNotNull(refreshed);
        Set<UserExercise> related = refreshed.getUserExercises();
        assertEquals(2, related.size());
    }

    @Test
    public void shouldDeleteUserCascadeExercises() {
        User user = new User("luke", "luke@example.com", "hash", "standard");
        ExerciseInfo exerciseInfo = new ExerciseInfo(0.8f, "Intense cardio");

        exerciseInfoRepository.save(exerciseInfo);

        UserExercise userExercise1 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 60, 600);
        UserExercise userExercise2 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-12T00:00:00"), 30, 300);

        user.getUserExercises().addAll(Set.of(userExercise1, userExercise2));
        userRepository.save(user);

        Long userId = user.getId();
        Long ex1Id = userExercise1.getUserExerciseId();
        Long ex2Id = userExercise2.getUserExerciseId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(userExerciseRepository.findById(ex1Id).isPresent());
        assertFalse(userExerciseRepository.findById(ex2Id).isPresent());
    }

    @Test
    public void shouldDeleteExerciseInfoCascadeExercises() {
        User user = new User("nina", "nina@example.com", "hash", "standard");
        userRepository.save(user);

        ExerciseInfo exerciseInfo = new ExerciseInfo(0.8f, "Dance fitness");

        UserExercise userExercise1 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 40, 300);
        UserExercise userExercise2 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-12T00:00:00"), 50, 400);

        exerciseInfo.getUserExercises().addAll(Set.of(userExercise1, userExercise2));
        exerciseInfoRepository.save(exerciseInfo);

        Long exerciseId = exerciseInfo.getExerciseId();
        Long userExercise1Id = userExercise1.getUserExerciseId();
        Long userExercise2Id = userExercise2.getUserExerciseId();

        exerciseInfoRepository.deleteById(exerciseId);

        assertFalse(exerciseInfoRepository.findById(exerciseId).isPresent());
        assertFalse(userExerciseRepository.findById(userExercise1Id).isPresent());
        assertFalse(userExerciseRepository.findById(userExercise2Id).isPresent());
    }

    @Test
    public void shouldNotSave2SameExercisesFromUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        ExerciseInfo exerciseInfo = new ExerciseInfo(0.8f, "Cardio");
        exerciseInfoRepository.save(exerciseInfo);

        UserExercise exerciseInfo1 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 30, 250);
        UserExercise exerciseInfo2 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 30, 250);

        user.getUserExercises().add(exerciseInfo1);
        user.getUserExercises().add(exerciseInfo2);

        userRepository.save(user);

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, saved.getUserExercises().size());
    }

    @Test
    public void shouldNotSave2SameExercisesFromExerciseInfo() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        ExerciseInfo exerciseInfo = new ExerciseInfo(0.8f, "Cardio");

        UserExercise exerciseInfo1 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 30, 250);
        UserExercise exerciseInfo2 = new UserExercise(user, exerciseInfo, LocalDateTime.parse("2025-05-11T00:00:00"), 30, 250);

        exerciseInfo.getUserExercises().add(exerciseInfo1);
        exerciseInfo.getUserExercises().add(exerciseInfo2);

        exerciseInfoRepository.save(exerciseInfo);

        ExerciseInfo saved = exerciseInfoRepository.findById(exerciseInfo.getExerciseId()).orElseThrow();
        assertEquals(1, saved.getUserExercises().size());
    }

}
