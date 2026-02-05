package com.bd2_team6.biteright.entities.daily_limits;

import com.bd2_team6.biteright.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "daily_limits")
@Getter
@Setter
@NoArgsConstructor
public class DailyLimits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_limit_id")
    private Long dailyLimitId;

    @Column(name = "calorie_limit")
    private Integer calorieLimit;

    @Column(name = "protein_limit")
    private Integer proteinLimit;

    @Column(name = "fat_limit")
    private Integer fatLimit;

    @Column(name = "carb_limit")
    private Integer carbLimit;

    @Column(name = "water_goal")
    private Integer waterGoal;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public DailyLimits(User user, Integer calorieLimit, Integer proteinLimit, Integer fatLimit, Integer carbLimit,
                       Integer waterGoal) {
        this.user = user;
        this.calorieLimit = calorieLimit;
        this.proteinLimit = proteinLimit;
        this.fatLimit = fatLimit;
        this.carbLimit = carbLimit;
        this.waterGoal = waterGoal;
    }
}
