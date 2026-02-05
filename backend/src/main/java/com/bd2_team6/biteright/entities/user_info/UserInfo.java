package com.bd2_team6.biteright.entities.user_info;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user_goal.UserGoal;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long userInfoId;

    @Column(name = "name")
    private String name;
    
    @Column(name = "surname")
    private String surname;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "weight")
    private Float weight; 
    
    @Column(name = "height")
    private Integer height; 
    
    @Column(name = "lifestyle")
    private String lifestyle; 

    @Column(name = "bmi")
    private Float bmi;      

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "user_goal_id")
    private UserGoal userGoal;

    public UserInfo(User user, UserGoal userGoal, String name, String surname, Integer age, Float weight,
                    Integer height, String lifestyle, Float bmi) {
        this.user = user;
        this.userGoal = userGoal;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.lifestyle = lifestyle;
        this.bmi = bmi;
    }
}
