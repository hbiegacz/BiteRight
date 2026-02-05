package com.bd2_team6.biteright.user_info;

import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.user_goal.UserGoal;
import com.bd2_team6.biteright.entities.user_goal.UserGoalRepository;
import com.bd2_team6.biteright.entities.user_info.UserInfo;
import com.bd2_team6.biteright.entities.user_info.UserInfoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserInfoTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGoalRepository userGoalRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveUserInfo() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "John", "Doe", 30, 70.5f, 180, "active", 21.8f);
        userInfoRepository.save(info);

        UserInfo found = userInfoRepository.findById(info.getUserInfoId()).orElse(null);
        assertNotNull(found);
        assertEquals("John", found.getName());
        assertEquals(180, found.getHeight());
    }

    @Test
    public void shouldUpdateUserInfo() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "Jane", "Doe", 28, 60.0f, 165, "moderate", 22.0f);
        userInfoRepository.save(info);

        info.setWeight(62.0f);
        info.setLifestyle("active");
        userInfoRepository.save(info);

        UserInfo updated = userInfoRepository.findById(info.getUserInfoId()).orElse(null);
        assertNotNull(updated);
        assertEquals(62.0f, updated.getWeight());
        assertEquals("active", updated.getLifestyle());
    }

    @Test
    public void shouldDeleteUserInfo() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "Mike", "Smith", 25, 80.0f, 175, "intense", 26.1f);
        userInfoRepository.save(info);

        userInfoRepository.delete(info);

        UserInfo deleted = userInfoRepository.findById(info.getUserInfoId()).orElse(null);
        assertNull(deleted);
    }

    @Test
    public void shouldFindUserInfoByUser() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "Alex", "Blue", 29, 75.0f, 178, "moderate", 23.7f);
        userInfoRepository.save(info);

        entityManager.flush();
        entityManager.clear();

        User foundUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("Alex", foundUser.getUserInfo().getName());
    }

    @Test
    public void shouldFindUserInfoByUserGoal() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "Kate", "W", 32, 55.0f, 160, "low", 21.5f);
        userInfoRepository.save(info);

        entityManager.flush();
        entityManager.clear();

        UserGoal foundGoal = userGoalRepository.findById(goal.getUserGoalId()).orElse(null);
        assertNotNull(foundGoal);
        assertEquals("Kate", foundGoal.getUserInfo().getName());
    }

    @Test
    public void shouldDeleteUserInfoWhenUserDeleted() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "Test", "Person", 40, 70.0f, 175, "low", 22.9f);

        user.setUserInfo(info);
        userInfoRepository.save(info);

        Integer infoId = info.getUserInfoId();
        Integer userId = user.getId();
        userRepository.delete(user);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(userInfoRepository.findById(infoId).isPresent());
    }

    @Test
    public void shouldDeleteUserInfoWhenUserGoalDeleted() {
        User user = new User("testuser", "test@example.com", "hashedpassword", "user");
        userRepository.save(user);

        UserGoal goal = new UserGoal("Gain Muscle", 80.0f, LocalDate.parse("2025-05-11"));
        userGoalRepository.save(goal);

        UserInfo info = new UserInfo(user, goal, "Test", "User", 35, 78.0f, 180, "moderate", 24.1f);

        goal.setUserInfo(info);
        userInfoRepository.save(info);

        Integer infoId = info.getUserInfoId();
        Integer userGoalId = goal.getUserGoalId();
        userGoalRepository.delete(goal);


        assertFalse(userGoalRepository.findById(userGoalId).isPresent());
        assertFalse(userInfoRepository.findById(infoId).isPresent());
    }
}
