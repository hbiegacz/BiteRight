-- --> mysql database summary:
-- created tables               18
-- created indexes               2
-- created views                 3
drop database mysql_database;
create database if not exists mysql_database;
use mysql_database;
-- ------------------------------------->        tables, primary keys, indexes & unique constraints    <---------------------------------------
create table address (
        address_id                  integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        address                     varchar(64) not null,
        city                        varchar(64) not null,
        postal_code                 varchar(5) not null,
        country                     varchar(64) not null
);

create table app_user (
        user_id                     integer unsigned not null auto_increment primary key,
        username                    varchar(64) not null,
        email                       varchar(64) not null,
        password_hash               varchar(255) not null,
        type                        varchar(64) not null,
        is_verified                 boolean default (false) not null,
        forgotten_password_code     varchar(64) not null
);

alter table app_user add constraint user_username_un unique ( username );

create table daily_limits (
        daily_limit_id              integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        calorie_limit               integer unsigned not null,
        protein_limit               integer unsigned not null,
        fat_limit                   integer unsigned not null,
        carb_limit                  integer unsigned not null,
        water_goal                  integer unsigned not null
);

create unique index daily_limits__idx on
        daily_limits (
                user_id
        asc );

create table exercise_info (
        exercise_id                 integer unsigned not null auto_increment primary key,
        metabolic_equivalent        decimal(4, 1) not null,
        name                        varchar(64) not null
);
alter table exercise_info add constraint exercise_name_un unique ( name );
alter table exercise_info add constraint not_negative_met CHECK(metabolic_equivalent >=0);

create table ingredient (
        ingredient_id               integer unsigned not null auto_increment primary key,
        name                        varchar(64) not null, -- UNIQUE
        brand                       varchar(64),
        portion_size                integer unsigned not null,
        calories                    integer unsigned not null,
        protein                     integer unsigned not null,
        fat                         integer unsigned not null,
        carbs                       integer unsigned not null
);

alter table ingredient add constraint ingredient_name_un unique ( name );


create table limit_history (
        history_id                  integer unsigned not null auto_increment primary key,
        date_changed                date not null,
        user_id                     integer unsigned not null,
        calorie_limit              integer unsigned not null,
        protein_limit               integer unsigned not null,
        fat_limit                   integer unsigned not null,
        carb_limit                  integer unsigned not null,
        water_goal                  integer unsigned
);

create table meal (
        meal_id                     integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        meal_type_id                integer unsigned not null,
        meal_date                   datetime not null,
        name                        varchar(64) not null,
        description                 varchar(256)
);

create table meal_content (
        meal_content_id             integer unsigned not null auto_increment primary key,
        ingredient_id               integer unsigned not null,
        meal_id                     integer unsigned not null,
        ingredient_amount           integer unsigned not null
);

create table meal_type (
        meal_type_id                integer unsigned not null auto_increment primary key,
        name                        varchar(64) not null -- UNIQUE
);

alter table meal_type add constraint unique_meal_type_name unique ( name );

create table recipe (
        recipe_id                   integer unsigned not null auto_increment primary key,
        name                        varchar(64) not null, -- UNIQUE
        description                 varchar(255),
        image_url                   varchar(511)
);

alter table recipe add constraint recipe_name_un unique ( name );

create table recipe_content (
        recipe_content_id           integer unsigned not null auto_increment primary key,
        recipe_id                   integer unsigned not null,
        ingredient_id               integer unsigned not null,
        ingredient_amount           integer unsigned not null
);

create table user_exercise (
        user_exercise_id            integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        exercise_id                 integer unsigned not null,
        activity_date               datetime not null,
        duration                    integer unsigned not null,
        calories_burnt              integer unsigned not null
);

create table user_goal (
        user_goal_id                integer unsigned not null auto_increment primary key,
        goal_type                   varchar(127) not null,
        goal_weight                 decimal(5, 2) not null,
        deadline                    date not null
);
alter table user_goal add constraint positive_goal_weight CHECK(goal_weight > 0);

create table user_info (
        user_info_id                integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        user_goal_id                integer unsigned not null,
        name                        varchar(64) not null,
        surname                     varchar(64) not null,
        age                         integer unsigned not null,
        weight                      decimal(5, 2) not null,
        height                      integer unsigned not null,
        lifestyle                   varchar(64) not null,
        bmi                         decimal(4, 2) not null
);

alter table user_info add constraint no_users_under_16 CHECK(age >= 16);
alter table user_info add constraint positive_usr_bmi CHECK(bmi > 0);
alter table user_info add constraint positive_usr_weight CHECK(weight > 0);


create table user_preferences (
        user_preferences_id         integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        language                    varchar(64) not null default('eng'),
        darkmode                    boolean not null default false,
        font                        varchar(64),
        notifications               boolean not null default false
);

create unique index user_preferences__idx on
        user_preferences (
                user_id
        asc );

create table water_intake (
        water_intake_id             integer unsigned not null auto_increment primary key,
        intake_date                 datetime not null,
        user_id                     integer unsigned not null,
        water_amount                integer unsigned not null
);

create table weight_history (
        weight_id                   integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        measurement_date            datetime not null,
        weight                      decimal(5, 2) not null
);

alter table weight_history add constraint positive_historic_weight CHECK(weight > 0);

create table verification_code (
        code_id                     integer unsigned not null auto_increment primary key,
        user_id                     integer unsigned not null,
        code                        varchar(64) not null,
        expiration_date             datetime not null
);



-- ---------------------------------------->        foreign keys     <------------------------------------------
alter table address
        add constraint address_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table daily_limits
        add constraint daily_limits_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table meal_content
        add constraint meal_content_ingredient_fk foreign key ( ingredient_id )
                references ingredient ( ingredient_id );

alter table meal_content
        add constraint meal_content_meal_fk foreign key ( meal_id )
                references meal ( meal_id );

alter table meal
        add constraint meal_meal_type_fk foreign key ( meal_type_id )
                references meal_type ( meal_type_id );

alter table meal
        add constraint meal_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table recipe_content
        add constraint recipe_content_food_fk foreign key ( ingredient_id )
                references ingredient ( ingredient_id );

alter table recipe_content
        add constraint recipe_content_recipe_fk foreign key ( recipe_id )
                references recipe ( recipe_id );

alter table user_exercise
        add constraint user_exercise_exercise_info_fk foreign key ( exercise_id )
                references exercise_info ( exercise_id );

alter table user_exercise
        add constraint user_exercise_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table user_info
        add constraint user_info_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table user_info
        add constraint user_info_user_goal_fk foreign key ( user_goal_id )
                references user_goal ( user_goal_id );

alter table user_preferences
        add constraint user_preferences_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table water_intake
        add constraint water_intake_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table weight_history
        add constraint weight_history_user_fk foreign key ( user_id )
                references app_user ( user_id );

alter table verification_code
        add constraint verification_code_user_fk foreign key ( user_id )
                references app_user ( user_id );


-- ---------------------------------------->        views     <------------------------------------------
create or replace view meal_info  as
select meal.meal_id                                                                     as meal_id,
        meal.user_id                                                                    as user_id,
    meal.name                                                                           as meal_name,
    ROUND(IFNULL(sum(ingredient.calories * meal_content.ingredient_amount / 100), 0))   as calories,
    ROUND(IFNULL(sum(ingredient.protein * meal_content.ingredient_amount / 100), 0))    as protein,
    ROUND(IFNULL(sum(ingredient.fat * meal_content.ingredient_amount / 100), 0))        as fat,
    ROUND(IFNULL(sum(ingredient.carbs * meal_content.ingredient_amount / 100), 0))      as carbs
from meal
    left join meal_content on meal.meal_id = meal_content.meal_id
    left join ingredient on ingredient.ingredient_id = meal_content.ingredient_id
group by meal.meal_id,
    meal.user_id,
    meal.name
;

CREATE OR REPLACE VIEW daily_summary AS
WITH --
water_info AS 
        (SELECT 
                user_id,
                intake_date AS summary_date,
                SUM(water_amount) AS water_drank
        FROM water_intake
        GROUP BY user_id, intake_date),
exercise_info AS 
        (SELECT 
                user_id,
                activity_date AS summary_date,
                SUM(calories_burnt) AS calories_burnt
        FROM user_exercise
        GROUP BY user_id, activity_date)

SELECT 
        u.user_id as user_id,
        COALESCE(m.meal_date, w.summary_date, e.summary_date)   AS summary_date,
        ROUND(IFNULL(SUM(mi.calories), 0))                    AS calories,
        ROUND(IFNULL(SUM(mi.protein), 0))                     AS protein,
        ROUND(IFNULL(SUM(mi.fat), 0))                         AS fat,
        ROUND(IFNULL(SUM(mi.carbs), 0))                       AS carbs,
        ROUND(IFNULL(SUM(w.water_drank), 0))                  AS water_drank,
        ROUND(IFNULL(SUM(e.calories_burnt), 0))               AS calories_burnt
FROM app_user u
        LEFT JOIN meal m ON u.user_id = m.user_id
        LEFT JOIN meal_info mi ON m.meal_id = mi.meal_id
        LEFT JOIN water_info w ON u.user_id = w.user_id AND m.meal_date = w.summary_date 
        LEFT JOIN exercise_info e ON u.user_id = e.user_id AND m.meal_date = e.summary_date
GROUP BY u.user_id, summary_date
UNION
        SELECT 
                w.user_id,
                w.summary_date,
                0, 0, 0, 0, -- empty cal, carbs, protein, fat values
                w.water_drank, 0
        FROM water_info w
        WHERE NOT EXISTS (
        SELECT 1 FROM meal m 
        WHERE m.user_id = w.user_id AND m.meal_date = w.summary_date)
UNION
        SELECT 
                e.user_id,
                e.summary_date,
                0, 0, 0, 0,
                0,
                e.calories_burnt
        FROM exercise_info e
        WHERE NOT EXISTS (
        SELECT 1 FROM meal m 
        WHERE m.user_id = e.user_id AND m.meal_date = e.summary_date);


create or replace view recipe_info ( recipe_id, recipe_name, calories, protein, fat, carbs ) as
select
        recipe.recipe_id   as recipe_id,
        recipe.name  as recipe_name,
	ROUND(IFNULL(sum(ingredient.calories * recipe_content.ingredient_amount / 100), 0))    as calories,
	ROUND(IFNULL(sum(ingredient.protein * recipe_content.ingredient_amount / 100), 0))     as protein,
	ROUND(IFNULL(sum(ingredient.fat * recipe_content.ingredient_amount / 100), 0))         as fat,
	ROUND(IFNULL(sum(ingredient.carbs * recipe_content.ingredient_amount / 100), 0))       as carbs
from
        recipe
        left join recipe_content on recipe.recipe_id = recipe_content.recipe_id
        left join ingredient on ingredient.ingredient_id = recipe_content.ingredient_id
group by
    recipe.recipe_id,
    recipe.name;