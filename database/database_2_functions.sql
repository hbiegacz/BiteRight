Use mysql_database;
DELIMITER $$
CREATE TRIGGER INSERT_HISTORICAL_LIMITS
BEFORE INSERT ON daily_limits
FOR EACH ROW
BEGIN
    INSERT INTO limit_history 
        (date_changed, user_id, calorie_limit, protein_limit, fat_limit, carb_limit, water_goal) 
    VALUES 
        (NOW(), NEW.user_id, NEW.calorie_limit, NEW.protein_limit, NEW.fat_limit, NEW.carb_limit, NEW.water_goal);
END $$

CREATE TRIGGER UPDATE_HISTORICAL_LIMITS
    BEFORE UPDATE ON daily_limits
    FOR EACH ROW
    BEGIN
        INSERT INTO limit_history 
            (date_changed, user_id, calorie_limit, protein_limit, fat_limit, carb_limit, water_goal) 
        VALUES 
            (NOW(), OLD.user_id, OLD.calorie_limit, OLD.protein_limit, OLD.fat_limit, OLD.carb_limit, OLD.water_goal );
    END $$

CREATE TRIGGER UPDATE_USER_WEIGHT_HISTORY
BEFORE UPDATE ON user_info
FOR EACH ROW
BEGIN
    IF OLD.weight != NEW.weight THEN
        INSERT INTO weight_history ( user_id,  measurement_date,  weight ) 
        VALUES ( NEW.user_id,  NOW(),  NEW.weight);
    END IF;
END $$

CREATE TRIGGER CALC_NEW_USER_BMI
BEFORE INSERT ON user_info
FOR EACH ROW 
BEGIN
    SET NEW.bmi = NEW.weight / (POWER(NEW.height / 100, 2));
END $$

CREATE TRIGGER UPDATE_USER_BMI
BEFORE UPDATE ON user_info
FOR EACH ROW 
BEGIN
    SET NEW.bmi = NEW.weight / (POWER(NEW.height / 100, 2));
END $$

CREATE TRIGGER CALC_CALORIES_BURNT
BEFORE INSERT ON user_exercise
FOR EACH ROW 
BEGIN
    DECLARE user_weight DECIMAL(5,2);
    DECLARE exercise_met DECIMAL(5,2);

    SELECT weight INTO user_weight FROM user_info WHERE user_id = NEW.user_id;
    SELECT metabolic_equivalent INTO exercise_met FROM exercise_info WHERE exercise_id = NEW.exercise_id;

	SET NEW.calories_burnt = ROUND(exercise_met * user_weight * NEW.duration / 60);
END $$

CREATE TRIGGER CALC_CALORIES_BURNT_UPDATE
BEFORE UPDATE ON user_exercise
FOR EACH ROW 
BEGIN
    DECLARE user_weight DECIMAL(5,2);
    DECLARE exercise_met DECIMAL(5,2);

    SELECT weight INTO user_weight
    FROM user_info
    WHERE user_id = NEW.user_id;

    SELECT metabolic_equivalent INTO exercise_met
    FROM exercise_info
    WHERE exercise_id = NEW.exercise_id;

    SET NEW.calories_burnt = ROUND(exercise_met * user_weight * NEW.duration / 60);
END $$

DELIMITER ;
