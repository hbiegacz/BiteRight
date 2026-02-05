package com.bd2_team6.biteright.exercise_info;

import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfoRepository;
import com.bd2_team6.biteright.entities.user_exercise.UserExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ExerciseInfoTests {

    @Autowired
    private ExerciseInfoRepository exerciseInfoRepository;

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @Test
    public void shouldSaveExerciseInfo() {
        ExerciseInfo exerciseInfo = new ExerciseInfo(8.0f, "Running");
        exerciseInfoRepository.save(exerciseInfo);

        ExerciseInfo foundExerciseInfo = exerciseInfoRepository.findById(exerciseInfo.getExerciseId()).orElse(null);
        assertNotNull(foundExerciseInfo);
        assertEquals(exerciseInfo.getMetabolicEquivalent(), foundExerciseInfo.getMetabolicEquivalent());
        assertEquals(exerciseInfo.getName(), foundExerciseInfo.getName());
    }

    @Test
    public void shouldUpdateExerciseInfo() {
        ExerciseInfo exerciseInfo = new ExerciseInfo(8.0f, "Running");
        exerciseInfoRepository.save(exerciseInfo);

        exerciseInfo.setMetabolicEquivalent(7.5f);
        exerciseInfo.setName("Jogging");
        exerciseInfoRepository.save(exerciseInfo);

        ExerciseInfo updatedExerciseInfo = exerciseInfoRepository.findById(exerciseInfo.getExerciseId()).orElse(null);
        assertNotNull(updatedExerciseInfo);
        assertEquals(7.5f, updatedExerciseInfo.getMetabolicEquivalent());
        assertEquals("Jogging", updatedExerciseInfo.getName());
    }

    @Test
    public void shouldDeleteExerciseInfo() {
        ExerciseInfo exerciseInfo = new ExerciseInfo(8.0f, "Running");
        exerciseInfoRepository.save(exerciseInfo);

        exerciseInfoRepository.delete(exerciseInfo);

        ExerciseInfo deletedExerciseInfo = exerciseInfoRepository.findById(exerciseInfo.getExerciseId()).orElse(null);
        assertNull(deletedExerciseInfo);
    }
}

