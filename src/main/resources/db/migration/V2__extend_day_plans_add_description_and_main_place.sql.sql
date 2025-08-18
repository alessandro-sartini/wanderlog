ALTER TABLE day_plans
  ADD COLUMN title VARCHAR(255) NULL,
  ADD COLUMN note TEXT NULL,
  ADD COLUMN main_place_name VARCHAR(255) NULL,
  ADD COLUMN main_place_address VARCHAR(500) NULL,
  ADD COLUMN main_place_place_id VARCHAR(128) NULL,
  ADD COLUMN main_place_latitude DOUBLE NULL,
  ADD COLUMN main_place_longitude DOUBLE NULL;
