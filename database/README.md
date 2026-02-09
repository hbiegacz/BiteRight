# Database Description
The BiteRight database consists of 18 core tables and 3 optimized views, creating a robust relational data architecture that handles multiple interconnected aspects of health and nutrition tracking through four distinct layers: the User Management Layer which handles authentication, profiles, and personal preferences; the Nutrition Tracking Layer that manages recipes, ingredients, meals, and dietary limits; the Health Monitoring Layer which tracks weight history, exercise data, and water intake; and the Analytics Layer that provides summarized views for performance optimization, all working together to support comprehensive user management, nutritional tracking, meal planning, exercise monitoring, and health analytics for the BiteRight application.

## TABLES
All the tables and views created for the sake of our application are defined in the following file: *database_1_initialization.sql*.
### 👤 app_user 
Stores user authentication and basic account information including user ID, username, email, password hash, and verification status.
Attributes:
>        user_id                  integer unsigned not null auto_increment primary key,
>        username                 varchar(64) not null,
>        email                    varchar(64) not null,
>        password_hash            varchar(255) not null,
>        type                     varchar(64) not null,
>        is_verified              boolean default (false) not null,
>        forgotten_password_code  varchar(64) not null

### 📝 user_info 
Contains detailed user profile data such as name, surname, age, weight, height, BMI, and links to user goals.
Attributes:
>        user_info_id             integer unsigned not null auto_increment primary key,
>        user_id                  integer unsigned not null,
>        user_goal_id             integer unsigned not null,
>        name                     varchar(64) not null,
>        surname                  varchar(64) not null,
>        age                      integer unsigned not null,
>        weight                   decimal(5, 2) not null,
>        height                   integer unsigned not null,
>        lifestyle                varchar(64) not null,
>        bmi                      decimal(4, 2) not null

###  ⚙️ user_preferences
Manages user application settings including language preference, dark mode status, font selection, and notification preferences.
Attributes:
>        user_preferences_id      integer unsigned not null auto_increment primary key,
>        user_id                  integer unsigned not null,
>        language                 varchar(64) not null default('eng'),
>        darkmode                 boolean not null default false,
>        font                     varchar(64),
>        notifications            boolean not null default false

### 🎯 user_goal 
Tracks user fitness goals with goal type, target weight, and deadline date.
Attributes:
>        user_goal_id             integer unsigned not null auto_increment primary key,
>        goal_type                varchar(127) not null,
>        goal_weight              decimal(5, 2) not null,
>        deadline                 date not null
### 🔐 verification_code 
Stores verification codes for user account validation with expiration dates.
Attributes:
>        code_id                  integer unsigned not null auto_increment primary key,
>        user_id                  integer unsigned not null,
>        code                     varchar(64) not null,
>        expiration_date          datetime not null

### 🏠 address 
Maintains user address information including street address, city, postal code, and country.
Attributes:
>     address_id                  integer unsigned not null auto_increment primary key,
>     user_id                     integer unsigned not null,
>     address                     varchar(64) not null,
>     city                        varchar(64) not null,
>     postal_code                 varchar(5) not null,
>     country                     varchar(64) not null    

### ⚖️ weight_history 
Records historical weight measurements for tracking progress over time.
Attributes:
>        weight_id                integer unsigned not null auto_increment primary key,
>        user_id                  integer unsigned not null,
>        measurement_date         datetime not null,
>        weight                   decimal(5, 2) not null
### 📏 daily_limits 
Defines user's daily nutritional targets including calorie, protein, fat, carbohydrate, and water intake goals.
Attributes:
>        daily_limit_id           integer unsigned not null auto_increment primary key,
>        user_id                  integer unsigned not null,
>        calorie_limit            integer unsigned not null,
>        protein_limit            integer unsigned not null,
>        fat_limit                integer unsigned not null,
>        carb_limit               integer unsigned not null,
>        water_goal               integer unsigned not null
### 📊  limit_history 
Maintains a history of changes to user's nutritional limits over time.
Attributes:
>        history_id               integer unsigned not null auto_increment primary key,
>        date_changed             date not null,
>        user_id                  integer unsigned not null,
>        calorie_limit            integer unsigned not null,
>        protein_limit            integer unsigned not null,
>        fat_limit                integer unsigned not null,
>        carb_limit               integer unsigned not null,
>        water_goal               integer unsigned
### 🍽️ recipe 
Stores recipe information including unique ID, name, and description.
Attributes:
>        recipe_id                integer unsigned not null auto_increment primary key,
>        name                     varchar(64) not null, -- UNIQUE
>        description              varchar(255),
>        image_url                varchar(255)
### 📜 recipe_content  
Links recipes with ingredients, specifying the amount of each ingredient used in a recipe.
Attributes:
>        recipe_content_id        integer unsigned not null auto_increment primary key,
>        recipe_id                integer unsigned not null,
>        ingredient_id            integer unsigned not null,
>        ingredient_amount        integer unsigned not null

### 🥕 ingredient 
Contains nutritional information for food ingredients including calories, protein, fat, and carbohydrates per portion.
Attributes:
>        ingredient_id            integer unsigned not null auto_increment primary key,
>        name                     varchar(64) not null, -- UNIQUE
>        brand                    varchar(64),
>        portion_size             integer unsigned not null,
>        calories                 integer unsigned not null,
>        protein                  integer unsigned not null,
>        fat                      integer unsigned not null,
>        carbs                    integer unsigned not null

### 🍽️ meal  
Tracks user meals with meal type, date consumed, name, and description.
Attributes:
>        meal_id                   integer unsigned not null auto_increment primary key,
>        user_id                   integer unsigned not null,
>        meal_type_id              integer unsigned not null,
>        meal_date                 datetime not null,
>        name                      varchar(64) not null,
>        description               varchar(256)

### 🍱 meal_content 
Links meals with ingredients, specifying the amount of each ingredient in a meal.
Attributes:
>        meal_content_id           integer unsigned not null auto_increment primary key,
>        ingredient_id             integer unsigned not null,
>        meal_id                   integer unsigned not null,
>        ingredient_amount         integer unsigned not null

### 🕐 meal_type 
Categorizes meals (breakfast, lunch, dinner, snack).
Attributes:
>        meal_type_id              integer unsigned not null auto_increment primary key,
>        name                      varchar(64) not null -- UNIQUE
### 💧 water_intake 
Records user water consumption with date and amount.
Attributes:
>        water_intake_id           integer unsigned not null auto_increment primary key,
>        intake_date               datetime not null,
>        user_id                   integer unsigned not null,
>        water_amount              integer unsigned not null
### 🏃 exercise_info  
Stores exercise types with metabolic equivalent values for calorie calculation.
Attributes:
>        exercise_id               integer unsigned not null auto_increment primary key,
>        metabolic_equivalent      decimal(4, 1) not null,
>        name                      varchar(64) not null
### 💪 user_exercise 
Tracks user workout sessions including exercise type, duration, and calories burned.
Attributes:
>        user_exercise_id          integer unsigned not null auto_increment primary key,
>        user_id                   integer unsigned not null,
>        exercise_id               integer unsigned not null,
>        activity_date             datetime not null,
>        duration                  integer unsigned not null,
>        calories_burnt            integer unsigned not null

## VIEWS
These views are designed to improve application performance by pre-calculating commonly accessed summary data.

### daily_summary
Provides aggregated daily nutrition data for quick access to user's daily consumption metrics.
Groups the following data:
>    u.user_id                                                                  as user_id, <br/>
>    IFNULL(m.meal_date, w.summary_date, e.summary_date)                        as summary_date, <br/>
>    IFNULL(SUM(mi.calories), 0)                                                as calories, <br/>
>    IFNULL(SUM(mi.protein), 0)                                                 as protein, <br/>
>    IFNULL(SUM(mi.fat), 0)                                                     as fat, <br/>
>    IFNULL(SUM(mi.carbs), 0)                                                   as carbs, <br/>
>    IFNULL(SUM(w.water_drank), 0)                                              as water_drank, <br/>
>    IFNULL(SUM(e.calories_burnt), 0)                                           as calories_burnt  

### recipe_info
Displays recipe nutritional information including calories, protein, fat, and carbohydrates.
Groups the following data:
>   recipe.recipe_id                                                            as recipe_id, <br/>
>   recipe.name                                                                 as recipe_name,  <br/>
>	IFNULL(sum(ingredient.calories * recipe_content.ingredient_amount /100), 0) as calories, <br/>
>	IFNULL(sum(ingredient.protein * recipe_content.ingredient_amount / 100), 0) as protein, <br/>
>	IFNULL(sum(ingredient.fat * recipe_content.ingredient_amount / 100), 0)     as fat,  <br/>
>	IFNULL(sum(ingredient.carbs * recipe_content.ingredient_amount / 100), 0)   as carbs       
### meal_info
Shows nutritional breakdown of meals for simplified meal tracking and analysis.
Groups the following data:
>    meal.meal_id                                                               as meal_id,  \
>    meal.user_id                                                               as user_id,  \
>    meal.name                                                                  as meal_name, \
>    IFNULL(sum(ingredient.calories * meal_content.ingredient_amount / 100), 0) as calories, \
>    IFNULL(sum(ingredient.protein * meal_content.ingredient_amount / 100), 0)  as protein,  \
>    IFNULL(sum(ingredient.fat * meal_content.ingredient_amount / 100), 0)      as fat,      \
>    IFNULL(sum(ingredient.carbs * meal_content.ingredient_amount / 100), 0)    as carbs     


## Database Triggers
All 8 of the triggers described below are defined in the following file: *database_2_functions.sql*.
### 📝 INSERT_HISTORICAL_LIMITS
Saves nutritional values to limit_history when new daily limits are created. Captures calories, protein, fat, carbs, and water goals with timestamp and user ID.

### 🗄️ UPDATE_HISTORICAL_LIMITS
Creates backup in limit_history when daily limits are modified. Preserves previous values before changes are applied.

### 📝 INSERT_USER_WEIGHT_HISTORY
Records initial weight in weight_history when new user profile is created. Captures starting point for weight tracking.

### ⚖️ UPDATE_USER_WEIGHT
Updates user_info.weight when new weight_history record is added. Keeps current weight synchronized in user profile.

### 📊 CALC_NEW_USER_BMI
Calculates BMI (weight/height² in meters) when new user profile is created. Formula: **BMI = weight / (height/100)²**.

### 📈 UPDATE_USER_BMI
Recalculates BMI when user's weight or height changes. Maintains accurate BMI value across profile updates.

### 🔥 CALC_CALORIES_BURNT
Calculates calories burned for new exercise entries using formula: **calories = MET × weight × duration ÷ 60**. Retrieves user weight and exercise MET value automatically.

### 🔄 CALC_CALORIES_BURNT_UPDATE
Recalculates calories burned when exercise details are modified. Ensures accurate energy expenditure data after workout record changes.