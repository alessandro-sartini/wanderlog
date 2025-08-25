-- V5: catalogo luoghi locali

CREATE TABLE places (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  provider VARCHAR(30) NOT NULL,
  provider_place_id VARCHAR(128) NOT NULL,
  name VARCHAR(255) NOT NULL,
  formatted_address VARCHAR(500),
  lat DOUBLE NOT NULL,
  lon DOUBLE NOT NULL,

  -- per ora semplice CSV; in futuro si può passare a JSON
  categories_csv TEXT,

  rating DOUBLE,
  user_ratings_total INT,
  price_level INT,

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_places_provider_pid (provider, provider_place_id),
  INDEX idx_places_name (name),
  INDEX idx_places_lat_lon (lat, lon)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
