package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.entities.user_goal.UserGoal;
import com.bd2_team6.biteright.entities.user_goal.UserGoalRepository;
import com.bd2_team6.biteright.entities.user_info.UserInfo;
import com.bd2_team6.biteright.entities.user_info.UserInfoRepository;
import com.bd2_team6.biteright.entities.user_preferences.UserPreferences;
import com.bd2_team6.biteright.entities.user_preferences.UserPreferencesRepository;
import com.bd2_team6.biteright.entities.verification_code.VerificationCode;
import com.bd2_team6.biteright.entities.verification_code.VerificationCodeRepository;
import com.bd2_team6.biteright.controllers.requests.RegistrationRequest;
import com.bd2_team6.biteright.controllers.responses.AvailabilityResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserInfoRepository userInfoRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailSendingService emailService;
    private final DailyLimitsService dailyLimitsService;

    @Value("${verification.code.expiration.minutes:60}")
    private int verificationCodeExpirationMinutes;

    public void registerNewUser(RegistrationRequest request) throws Exception {
        // Validate basic registration fields
        validateEmail(request.getEmail());
        validateUsername(request.getUsername());
        validatePassword(request.getPassword());

        // Check if user already exists
        Optional<User> userOptByUsername = userRepository.findByUsername(request.getUsername());
        Optional<User> userOptByEmail = userRepository.findByEmail(request.getEmail());

        if (userOptByUsername.isPresent())
            throw new Exception("Username " + request.getUsername() + " already taken.");

        if (userOptByEmail.isPresent())
            throw new Exception("Email " + request.getEmail() + " already taken.");

        // Validate onboarding data if provided
        if (hasOnboardingData(request)) {
            validateOnboardingData(request);
        }

        // Create user
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(request.getUsername(), request.getEmail(), hashedPassword, "user");
        userRepository.save(newUser);

        // Create UserGoal with provided data or defaults
        UserGoal newUserGoal = createUserGoal(request);
        userGoalRepository.save(newUserGoal);

        // Create UserInfo with provided data or defaults
        UserInfo userInfo = createUserInfo(newUser, newUserGoal, request);
        userInfoRepository.save(userInfo);

        // Create UserPreferences with defaults
        UserPreferences newUserPreferences = new UserPreferences(newUser, "english", true, "arial", true);
        userPreferencesRepository.save(newUserPreferences);

        // Create DailyLimits with provided data or defaults
        createDailyLimits(newUser, request);

        // Send verification email
        String code = emailService.generateVeryficationCode();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(60);
        VerificationCode newCode = new VerificationCode(code, expirationDate, newUser);
        verificationCodeRepository.save(newCode);

        emailService.sendVerificationEmail(newUser.getUsername(), newUser.getEmail(), code);
    }

    private boolean hasOnboardingData(RegistrationRequest request) {
        return request.getName() != null || request.getSurname() != null || request.getAge() != null;
    }

    private void validateOnboardingData(RegistrationRequest request) throws Exception {
        // Personal info validation
        if (request.getName() != null && (request.getName().trim().isEmpty() || request.getName().length() > 50)) {
            throw new Exception("Name must be between 1 and 50 characters.");
        }
        if (request.getSurname() != null
                && (request.getSurname().trim().isEmpty() || request.getSurname().length() > 50)) {
            throw new Exception("Surname must be between 1 and 50 characters.");
        }
        if (request.getAge() != null && (request.getAge() < 13 || request.getAge() > 120)) {
            throw new Exception("Age must be between 13 and 120.");
        }

        // Body stats validation
        if (request.getWeight() != null && (request.getWeight() < 20 || request.getWeight() > 500)) {
            throw new Exception("Weight must be between 20 and 500 kg.");
        }
        if (request.getHeight() != null && (request.getHeight() < 100 || request.getHeight() > 250)) {
            throw new Exception("Height must be between 100 and 250 cm.");
        }
        if (request.getBmi() != null && (request.getBmi() < 10 || request.getBmi() > 100)) {
            throw new Exception("BMI must be between 10 and 100.");
        }

        // Lifestyle validation
        if (request.getLifestyle() != null) {
            String lifestyle = request.getLifestyle().toLowerCase();
            if (!lifestyle.equals("sedentary") && !lifestyle.equals("light") &&
                    !lifestyle.equals("moderate") && !lifestyle.equals("active") &&
                    !lifestyle.equals("athlete")) {
                throw new Exception("Lifestyle must be one of: sedentary, light, moderate, active, athlete.");
            }
        }

        // Goal validation
        if (request.getGoalType() != null) {
            String goalType = request.getGoalType().toLowerCase();
            if (!goalType.equals("lose") && !goalType.equals("maintain") && !goalType.equals("gain")) {
                throw new Exception("Goal type must be one of: lose, maintain, gain.");
            }
        }
        if (request.getGoalWeight() != null && (request.getGoalWeight() < 20 || request.getGoalWeight() > 500)) {
            throw new Exception("Goal weight must be between 20 and 500 kg.");
        }

        // Daily limits validation
        if (request.getCalorieLimit() != null
                && (request.getCalorieLimit() < 800 || request.getCalorieLimit() > 10000)) {
            throw new Exception("Calorie limit must be between 800 and 10000.");
        }
        if (request.getProteinLimit() != null && (request.getProteinLimit() < 0 || request.getProteinLimit() > 1000)) {
            throw new Exception("Protein limit must be between 0 and 1000 grams.");
        }
        if (request.getCarbLimit() != null && (request.getCarbLimit() < 0 || request.getCarbLimit() > 2000)) {
            throw new Exception("Carb limit must be between 0 and 2000 grams.");
        }
        if (request.getFatLimit() != null && (request.getFatLimit() < 0 || request.getFatLimit() > 500)) {
            throw new Exception("Fat limit must be between 0 and 500 grams.");
        }
        if (request.getWaterGoal() != null && (request.getWaterGoal() < 0 || request.getWaterGoal() > 10000)) {
            throw new Exception("Water goal must be between 0 and 10000 ml.");
        }
    }

    private UserGoal createUserGoal(RegistrationRequest request) {
        String goalType = request.getGoalType() != null ? request.getGoalType() : "Maintain weight";
        Float goalWeight = request.getGoalWeight() != null ? request.getGoalWeight() : 75f;
        LocalDate deadline;

        if (request.getGoalDate() != null && !request.getGoalDate().isEmpty()) {
            try {
                deadline = LocalDate.parse(request.getGoalDate());
            } catch (Exception e) {
                deadline = LocalDate.now().plusMonths(3);
            }
        } else {
            deadline = LocalDate.now().plusMonths(3);
        }

        return new UserGoal(goalType, goalWeight, deadline);
    }

    private UserInfo createUserInfo(User user, UserGoal userGoal, RegistrationRequest request) {
        String name = request.getName() != null ? request.getName() : "John";
        String surname = request.getSurname() != null ? request.getSurname() : "Doe";
        Integer age = request.getAge() != null ? request.getAge() : 25;
        Float weight = request.getWeight() != null ? request.getWeight() : 80f;
        Integer height = request.getHeight() != null ? request.getHeight() : 180;
        String lifestyle = request.getLifestyle() != null ? request.getLifestyle() : "active";
        Float bmi = request.getBmi() != null ? request.getBmi() : 24.69f;

        return new UserInfo(user, userGoal, name, surname, age, weight, height, lifestyle, bmi);
    }

    private void createDailyLimits(User user, RegistrationRequest request) {
        Integer calorieLimit = request.getCalorieLimit() != null ? request.getCalorieLimit() : 2000;
        Integer proteinLimit = request.getProteinLimit() != null ? request.getProteinLimit() : 150;
        Integer carbLimit = request.getCarbLimit() != null ? request.getCarbLimit() : 250;
        Integer fatLimit = request.getFatLimit() != null ? request.getFatLimit() : 65;
        Integer waterGoal = request.getWaterGoal() != null ? request.getWaterGoal() : 2500;

        dailyLimitsService.createDailyLimitsForUser(user, calorieLimit, proteinLimit, carbLimit, fatLimit, waterGoal);
    }

    private void validateUsername(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username cannot be empty.");
            throw new Exception("Username cannot be empty.");
        }
        if (username.length() < 3 || username.length() > 50) {
            logger.error("Username must be between 3 and 50 characters.");
            throw new Exception("Username must be between 3 and 50 characters.");
        }
    }

    private void validatePassword(String password) throws Exception {
        if (password == null || password.isEmpty()) {
            logger.error("Password cannot be empty.");
            throw new Exception("Password cannot be empty.");
        }
        if (password.length() < 6) {
            logger.error("Password must be at least 6 characters long.");
            throw new Exception("Password must be at least 6 characters long.");
        }
    }

    public AvailabilityResponse checkAvailability(String username, String email) {
        boolean usernameAvailable = true;
        boolean emailAvailable = true;
        String message = "";

        if (username != null && !username.trim().isEmpty()) {
            Optional<User> userByUsername = userRepository.findByUsername(username);
            if (userByUsername.isPresent()) {
                usernameAvailable = false;
                message = "Username already taken.";
                logger.error("Username already taken.");
            }
        }

        if (email != null && !email.trim().isEmpty()) {
            Optional<User> userByEmail = userRepository.findByEmail(email);
            if (userByEmail.isPresent()) {
                emailAvailable = false;
                if (!message.isEmpty()) {
                    message += " Email already registered.";
                } else {
                    message = "Email already registered.";
                }
                logger.error("Email already registered.");
            }
        }

        if (usernameAvailable && emailAvailable) {
            message = "Username and email are available.";
        }

        return new AvailabilityResponse(usernameAvailable, emailAvailable, message);
    }

    public void loginUser(String email, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.error("User with email " + email + " not found.");
            throw new Exception("User with email " + email + " not found.");
        }

        User user = userOpt.get();

        // Moving authentication check BEFORE verification check
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        if (auth == null || !auth.isAuthenticated()) {
            throw new Exception("Invalid password.");
        }

        if (!user.getIsVerified()) {
            logger.info("User with email " + email + " is not verified. Resending verification email.");

            // Refresh verification code
            Optional<VerificationCode> codeOpt = verificationCodeRepository.findByUser(user);
            VerificationCode verificationCode;
            String newCodeValue = emailService.generateVeryficationCode();
            LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(verificationCodeExpirationMinutes);

            if (codeOpt.isPresent()) {
                verificationCode = codeOpt.get();
                verificationCode.setCode(newCodeValue);
                verificationCode.setExpirationDate(expirationDate);
            } else {
                verificationCode = new VerificationCode(newCodeValue, expirationDate, user);
            }
            verificationCodeRepository.save(verificationCode);

            // Resend email
            emailService.sendVerificationEmail(user.getUsername(), user.getEmail(), newCodeValue);

            throw new Exception(
                    "User with email " + email + " is not verified. A new verification email has been sent.");
        }

        logger.info("User authenticated successfully.");
    }

    public void validateEmail(String email) throws Exception {
        String correctRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (email == null || !email.matches(correctRegex)) {
            logger.error("Invalid email");
            throw new Exception("Invalid email");
        }
    }

    public String getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty())
            return "No users found.";
        else {
            String usersList = "";
            for (User user : users) {
                usersList += user.getUsername() + " " + user.getEmail() + " " + user.getIsVerified() + " "
                        + user.getVerificationCode().getCode() + "\n";
            }
            return usersList;
        }
    }

    public void verifyUser(String email, String recivedVerificationCode) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " not found.");

        User user = userOpt.get();
        if (user.getIsVerified())
            throw new RuntimeException("User with email " + email + " is already verified.");
        Optional<VerificationCode> codeOpt = verificationCodeRepository.findByUser(user);
        if (codeOpt.isEmpty())
            throw new RuntimeException("Verification code for user with email " + email + " not found.");

        VerificationCode correctVerificationCode = codeOpt.get();
        if (!correctVerificationCode.getCode().equals(recivedVerificationCode))
            throw new RuntimeException("Invalid verification code for user with email " + email + ".");

        if (correctVerificationCode.isExpired()) {
            correctVerificationCode.setCode(emailService.generateVeryficationCode());
            correctVerificationCode.setExpirationDate(LocalDateTime.now().plusMinutes(60));
            verificationCodeRepository.save(correctVerificationCode);
            emailService.sendVerificationEmail(user.getUsername(), email, correctVerificationCode.getCode());
            throw new RuntimeException("Verification code for user with email " + email
                    + " has expired. We have sent you another verification email.");
        }

        user.setIsVerified(true);
        userRepository.save(user);
        logger.info("User with email " + email + " verified successfully.");
    }

    public void changeUsername(String email, String newUsername) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " not found.");
        User user = userOpt.get();
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void changeEmail(String email, String newEmail) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " not found.");
        User user = userOpt.get();
        validateEmail(newEmail);
        user.setEmail(newEmail);
        userRepository.save(user);
        logger.info("Email for user with email " + email + " changed to " + newEmail + ".");
    }

    public void changePassword(String email, String oldPassword, String newPassword) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " not found.");

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, oldPassword));

        if (auth == null || !auth.isAuthenticated())
            throw new RuntimeException("Invalid password for user with email " + email + ".");

        User user = userOpt.get();
        String newHashedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newHashedPassword);
        userRepository.save(user);
        logger.info("Password for user with email " + email + " changed successfully.");
    }

    public void manageForgottenPassword(String email) throws RuntimeException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " was not found.");
        User user = userOpt.get();
        emailService.sendForgotPasswordEmail(user.getUsername(), email, user.getForgottenPasswordCode());
    }

    public void verifyForgottenPasswordCode(String email, String ForgottenPasswordCode) throws RuntimeException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " was not found.");
        User user = userOpt.get();

        if (!ForgottenPasswordCode.equals(user.getForgottenPasswordCode())) {
            logger.error("Incorrect reset code provided for user with email " + email + ".");
            throw new RuntimeException("Incorrect reset code provided.");
        }
    }

    public void resetForgottenPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent())
            throw new RuntimeException("User with email " + email + " was not found.");
        User user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.regeneratePasswordCode();
        userRepository.save(user);
        logger.info("Successfully changed user's password.");
    }
}
