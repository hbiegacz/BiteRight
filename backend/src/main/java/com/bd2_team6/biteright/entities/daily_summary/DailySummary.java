package com.bd2_team6.biteright.entities.daily_summary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "daily_summary")
@IdClass(DailySummaryId.class)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DailySummary {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "summary_date")
    private LocalDate summaryDate;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "protein")
    private Integer protein;

    @Column(name = "fat")
    private Integer fat;

    @Column(name = "carbs")
    private Integer carbs;

    @Column(name="water_drank")
    private Integer waterDrank;

    @Column(name="calories_burnt")
    private Integer caloriesBurnt;

}
