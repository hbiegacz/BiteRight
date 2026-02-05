package com.bd2_team6.biteright.entities.water_intake;

import com.bd2_team6.biteright.entities.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_intake")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class WaterIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "water_intake_id")
    private Long waterIntakeId;

    @Column(name = "intake_date")
    private LocalDateTime intakeDate;

    @Column(name = "water_amount")
    private Integer waterAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public WaterIntake(LocalDateTime intakeDate, User user, Integer waterAmount) {
        this.intakeDate = intakeDate;
        this.user = user;
        this.waterAmount = waterAmount;
    }
}
