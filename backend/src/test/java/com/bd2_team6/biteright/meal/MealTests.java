package com.bd2_team6.biteright.meal;

import com.bd2_team6.biteright.entities.meal.Meal;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.meal_type.MealType;
import com.bd2_team6.biteright.entities.meal.MealRepository;
import com.bd2_team6.biteright.entities.user.UserRepository;
import com.bd2_team6.biteright.entities.meal_type.MealTypeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MealTests {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveMeal() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        MealType mealType = new MealType("Breakfast");
        mealTypeRepository.save(mealType);

        Meal meal = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Pancakes", "Delicious pancakes with syrup");
        mealRepository.save(meal);

        Meal foundMeal = mealRepository.findById(meal.getMealId()).orElse(null);
        assertNotNull(foundMeal);
        assertEquals(meal.getName(), foundMeal.getName());
        assertEquals(meal.getMealDate(), foundMeal.getMealDate());
        assertEquals(meal.getUser().getUsername(), foundMeal.getUser().getUsername());
        assertEquals(meal.getMealType().getName(), foundMeal.getMealType().getName());
        assertEquals(meal.getDescription(), foundMeal.getDescription());
    }

    @Test
    public void shouldUpdateMeal() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        MealType mealType = new MealType("Dinner");
        mealTypeRepository.save(mealType);

        Meal meal = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Pasta", "Pasta with tomato sauce");
        mealRepository.save(meal);

        meal.setName("Spaghetti");
        meal.setDescription("Spaghetti with meatballs");
        mealRepository.save(meal);

        Meal updatedMeal = mealRepository.findById(meal.getMealId()).orElse(null);
        assertNotNull(updatedMeal);
        assertEquals("Spaghetti", updatedMeal.getName());
        assertEquals("Spaghetti with meatballs", updatedMeal.getDescription());
    }

    @Test
    public void shouldDeleteMeal() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        MealType mealType = new MealType("Lunch");
        mealTypeRepository.save(mealType);

        Meal meal = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Salad", "Fresh salad with vegetables");
        mealRepository.save(meal);

        mealRepository.delete(meal);

        Meal deletedMeal = mealRepository.findById(meal.getMealId()).orElse(null);
        assertNull(deletedMeal);
    }

    @Test
    public void shouldFindMealsByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        MealType mealType = new MealType("Snack");
        mealTypeRepository.save(mealType);

        Meal meal1 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");
        Meal meal2 = new Meal(user, mealType, LocalDateTime.parse("2025-05-12T00:00:00"), "Fruit", "Mixed fruit salad");
        mealRepository.save(meal1);
        mealRepository.save(meal2);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        Set<Meal> meals = refreshedUser.getMeals();
        assertEquals(2, meals.size());
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Cookies")));
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Fruit")));
    }

    @Test
    public void shouldFindMealsByMealType() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        MealType mealType = new MealType("lunch");
        mealTypeRepository.save(mealType);

        Meal meal1 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");
        Meal meal2 = new Meal(user, mealType, LocalDateTime.parse("2025-05-12T00:00:00"), "Fruit", "Mixed fruit salad");
        mealRepository.save(meal1);
        mealRepository.save(meal2);

        entityManager.flush();
        entityManager.clear();

        MealType refreshedMealType = mealTypeRepository.findById(mealType.getTypeId()).orElse(null);
        assertNotNull(refreshedMealType);

        Set<Meal> meals = refreshedMealType.getMeals();
        assertEquals(2, meals.size());
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Cookies")));
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Fruit")));
    }

    @Test
    public void shouldDeleteAddressesWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");

        MealType mealType = new MealType("lunch");
        mealTypeRepository.save(mealType);

        Meal meal1 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");
        Meal meal2 = new Meal(user, mealType, LocalDateTime.parse("2025-05-12T00:00:00"), "Fruit", "Mixed fruit salad");

        user.getMeals().add(meal1);
        user.getMeals().add(meal2);

        userRepository.save(user);

        Long userId = user.getId();
        Long meal1Id = meal1.getMealId();
        Long meal2Id = meal1.getMealId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(mealRepository.findById(meal1Id).isPresent());
        assertFalse(mealRepository.findById(meal2Id).isPresent());
    }

    @Test
    public void shouldDeleteAddressesWhenMealTypeDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");
        userRepository.save(user);

        MealType mealType = new MealType("lunch");

        Meal meal1 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");
        Meal meal2 = new Meal(user, mealType, LocalDateTime.parse("2025-05-12T00:00:00"), "Fruit", "Mixed fruit salad");

        mealType.getMeals().add(meal1);
        mealType.getMeals().add(meal2);

        mealTypeRepository.save(mealType);

        Long mealTypeId = mealType.getTypeId();
        Long meal1Id = meal1.getMealId();
        Long meal2Id = meal1.getMealId();

        mealTypeRepository.deleteById(mealTypeId);

        assertFalse(mealTypeRepository.findById(mealTypeId).isPresent());
        assertFalse(mealRepository.findById(meal1Id).isPresent());
        assertFalse(mealRepository.findById(meal2Id).isPresent());
    }

    @Test
    public void shouldNotSave2SameMealsFromUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        MealType mealType = new MealType("lunch");

        Meal meal1 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");
        Meal meal2 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");

        user.getMeals().add(meal1);
        user.getMeals().add(meal2);

        userRepository.save(user);

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, saved.getMeals().size());
    }

    @Test
    public void shouldNotSave2SameMealsFromMealType() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        MealType mealType = new MealType("lunch");

        Meal meal1 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");
        Meal meal2 = new Meal(user, mealType, LocalDateTime.parse("2025-05-11T00:00:00"), "Cookies", "Chocolate chip cookies");

        mealType.getMeals().add(meal1);
        mealType.getMeals().add(meal2);

        mealTypeRepository.save(mealType);

        MealType saved = mealTypeRepository.findById(mealType.getTypeId()).orElseThrow();
        assertEquals(1, saved.getMeals().size());
    }
}
