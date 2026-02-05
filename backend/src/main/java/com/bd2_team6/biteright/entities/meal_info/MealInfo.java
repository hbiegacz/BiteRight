package com.bd2_team6.biteright.entities.meal_info;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.*;

@Entity
@Immutable
@Table(name = "meal_info")
@Getter
@Setter
@NoArgsConstructor
public class MealInfo {
    @Id
    @Column(name = "meal_id")
    private Long mealId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "meal_name")
    private String mealName;

    @Column(name = "calories")
    private Float calories;

    @Column(name = "protein")
    private Float protein;

    @Column(name = "fat")
    private Float fat;

    @Column(name = "carbs")
    private Float carbs;

    public MealInfo(Long userId, String mealName, Float calories, Float protein, Float fat, Float carbs) {
        this.userId = userId;
        this.mealName = mealName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
}
