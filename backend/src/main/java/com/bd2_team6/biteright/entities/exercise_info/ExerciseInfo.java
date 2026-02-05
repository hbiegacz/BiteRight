package com.bd2_team6.biteright.entities.exercise_info;

import com.bd2_team6.biteright.entities.user_exercise.UserExercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercise_info")
@Getter
@Setter
@NoArgsConstructor
public class ExerciseInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "metabolic_equivalent")
    private Float metabolicEquivalent;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "exerciseInfo", cascade = CascadeType.ALL)
    private Set<UserExercise> userExercises = new HashSet<>();

    public ExerciseInfo(Float metabolicEquivalent, String name) {
        this.metabolicEquivalent = metabolicEquivalent;
        this.name = name;
    }
}
