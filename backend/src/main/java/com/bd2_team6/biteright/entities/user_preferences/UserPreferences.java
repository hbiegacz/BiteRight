package com.bd2_team6.biteright.entities.user_preferences;

import com.bd2_team6.biteright.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_preferences_id")
    private Long userPreferencesId;

    @Column(name = "language")
    private String language;  
    
    @Column(name = "darkmode")
    private Boolean darkmode; 
    
    @Column(name = "font")
    private String font;
    
    @Column(name = "notifications")
    private Boolean notifications; 

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserPreferences(User user, String language, Boolean darkmode, String font, Boolean notifications) {
        this.user = user;
        this.language = language;
        this.darkmode = darkmode;
        this.font = font;
        this.notifications = notifications;
    }
}
