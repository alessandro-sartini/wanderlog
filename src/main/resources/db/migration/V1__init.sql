-- V1: schema iniziale TravelSage (MySQL)
-- Charset/engine consigliati
-- NB: se il DB non è utf8mb4, imposta a livello di server o di schema.

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(120) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS trips (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  start_date DATE,
  end_date DATE,
  visibility VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_trips_owner FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_trips_owner_visibility ON trips(owner_id, visibility);
CREATE INDEX idx_trips_visibility ON trips(visibility);

CREATE TABLE IF NOT EXISTS day_plans (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  trip_id BIGINT NOT NULL,
  date DATE,
  index_in_trip INT NOT NULL,
  CONSTRAINT fk_day_plans_trip FOREIGN KEY (trip_id) REFERENCES trips(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_day_plans_trip_index ON day_plans(trip_id, index_in_trip);

CREATE TABLE IF NOT EXISTS activities (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  day_plan_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  start_time TIME,
  duration_minutes INT,
  order_index INT,
  latitude DOUBLE,
  longitude DOUBLE,
  place_id VARCHAR(128),
  address VARCHAR(500),
  travel_time_from_prev_minutes INT,
  travel_distance_from_prev_meters INT,
  CONSTRAINT fk_activities_day FOREIGN KEY (day_plan_id) REFERENCES day_plans(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_activities_day_order ON activities(day_plan_id, order_index);

CREATE TABLE IF NOT EXISTS tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  type VARCHAR(50),
  slug VARCHAR(120)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity_tags (
  activity_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (activity_id, tag_id),
  CONSTRAINT fk_acttags_activity FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE,
  CONSTRAINT fk_acttags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS media_attachments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  url VARCHAR(1000) NOT NULL,
  mime_type VARCHAR(120),
  note VARCHAR(500),
  CONSTRAINT fk_media_activity FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


