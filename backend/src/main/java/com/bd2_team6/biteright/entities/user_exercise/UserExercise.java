package com.bd2_team6.biteright.entities.user_exercise;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;
import com.bd2_team6.biteright.entities.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_exercise")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_exercise_id")
    private Long userExerciseId;

    @Column(name = "activity_date")
    private LocalDateTime activityDate;

    @Column(name ="duration")
    private Integer duration;        

    @Column(name = "calories_burnt")
    private Integer caloriesBurnt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private ExerciseInfo exerciseInfo;

    public UserExercise(User user, ExerciseInfo exerciseInfo, LocalDateTime activityDate, Integer duration, Integer caloriesBurnt) {
        this.user = user;
        this.exerciseInfo = exerciseInfo;
        this.activityDate = activityDate;
        this.duration = duration;
        this.caloriesBurnt = caloriesBurnt;
    }
        public UserExercise(User user, ExerciseInfo exerciseInfo, LocalDateTime activityDate, Integer duration) {
        this.user = user;
        this.exerciseInfo = exerciseInfo;
        this.activityDate = activityDate;
        this.duration = duration;
    }
}
