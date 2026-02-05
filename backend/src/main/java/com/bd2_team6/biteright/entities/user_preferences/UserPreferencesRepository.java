package com.bd2_team6.biteright.entities.user_preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    
}
