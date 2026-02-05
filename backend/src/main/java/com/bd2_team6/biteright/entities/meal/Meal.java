package com.bd2_team6.biteright.entities.meal;

import com.bd2_team6.biteright.entities.meal_content.MealContent;
import com.bd2_team6.biteright.entities.meal_type.MealType;
import com.bd2_team6.biteright.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meal")
@Getter
@Setter
@NoArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Integer mealId;

    @Column(name = "meal_date")
    private LocalDateTime mealDate;

    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MealContent> mealContents = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "meal_type_id")
    private MealType mealType;

    public Meal(User user, MealType mealType, LocalDateTime mealDate, String name, String description) {
        this.user = user;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meal)) return false;
        Meal meal = (Meal) o;
        return mealId != null && mealId.equals(meal.mealId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
