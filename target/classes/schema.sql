CREATE TABLE IF NOT EXISTS body_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    height REAL NOT NULL,
    weight REAL NOT NULL,
    age INTEGER NOT NULL,
    activity_level VARCHAR(20) NOT NULL DEFAULT 'MODERATE',
    bmi REAL,
    bmr REAL,
    recommended_calories_min INTEGER,
    recommended_calories_max INTEGER,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS weight_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    weight REAL NOT NULL,
    record_date DATE NOT NULL,
    create_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS recipe (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    ingredients TEXT,
    steps TEXT,
    protein REAL,
    carbohydrate REAL,
    fat REAL,
    calories INTEGER,
    image_url VARCHAR(500),
    description TEXT,
    source VARCHAR(100),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS meal_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    recipe_id INTEGER NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    portion REAL NOT NULL DEFAULT 1.0,
    portion_unit VARCHAR(20) NOT NULL DEFAULT '份',
    record_date DATE NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipe(id)
);

CREATE TABLE IF NOT EXISTS nutrition_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    record_date DATE NOT NULL,
    protein REAL DEFAULT 0,
    carbohydrate REAL DEFAULT 0,
    fat REAL DEFAULT 0,
    calories INTEGER DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS cycle_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    start_date DATE NOT NULL,
    end_date DATE,
    cycle_length INTEGER NOT NULL DEFAULT 28,
    phase VARCHAR(20),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS user_preference (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    preferences TEXT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS health_goal (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL DEFAULT 1,
    goal_type VARCHAR(20) NOT NULL,
    target_weight REAL,
    target_date DATE,
    current_progress REAL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);
