package com.bd2_team6.biteright.entities.user;

import com.bd2_team6.biteright.entities.address.Address;
import com.bd2_team6.biteright.entities.daily_limits.DailyLimits;
import com.bd2_team6.biteright.entities.limit_history.LimitHistory;
import com.bd2_team6.biteright.entities.meal.Meal;
import com.bd2_team6.biteright.entities.user_exercise.UserExercise;
import com.bd2_team6.biteright.entities.user_info.UserInfo;
import com.bd2_team6.biteright.entities.user_preferences.UserPreferences;
import com.bd2_team6.biteright.entities.verification_code.VerificationCode;
import com.bd2_team6.biteright.entities.water_intake.WaterIntake;
import com.bd2_team6.biteright.entities.weight_history.WeightHistory;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "type")
    private String type;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "forgotten_password_code")
    private String forgottenPasswordCode;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private DailyLimits dailyLimits;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserPreferences userPreferences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<LimitHistory> limitHistories = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VerificationCode verificationCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Address> addresses = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserInfo userInfo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Meal> meals = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<WaterIntake> waterIntakes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserExercise> userExercises = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<WeightHistory> weightHistories = new HashSet<>();


    public User(String username, String email, String passwordHash, String type) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.type = type;
        this.isVerified = false;
        this.forgottenPasswordCode = this.generatePasswordCode();
    }

    private String generatePasswordCode(){
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(100_000_000); 
        return String.format("%08d", code); 
    }

    public void regeneratePasswordCode(){
        this.forgottenPasswordCode = generatePasswordCode(); 
    }
}
