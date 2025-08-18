/* --- DAY_PLANS: rendi unico (trip_id, index_in_trip) senza violare la FK --- */

/* 1) Aggiungi PRIMA l'indice unico nuovo */
ALTER TABLE day_plans
  ADD UNIQUE KEY uk_day_plans_trip_idx (trip_id, index_in_trip);

/* 2) Poi puoi togliere il vecchio non-unico */
ALTER TABLE day_plans
  DROP INDEX idx_day_plans_trip_index;


/* --- ACTIVITIES: mantieni sempre un indice sulla FK day_plan_id --- */

/* 3) Crea un indice temporaneo sulla sola FK per non restare scoperti */
CREATE INDEX idx_activities_day ON activities(day_plan_id);

/* 4) Ora puoi droppare il vecchio composito basato su order_index */
DROP INDEX idx_activities_day_order ON activities;

/* 5) Rinomina le colonne per allineare al modello */
ALTER TABLE activities
  RENAME COLUMN order_index   TO order_in_day,
  RENAME COLUMN latitude      TO place_latitude,
  RENAME COLUMN longitude     TO place_longitude,
  RENAME COLUMN place_id      TO place_place_id,
  RENAME COLUMN address       TO place_address;

/* 6) Aggiungi i campi mancanti */
ALTER TABLE activities
  ADD COLUMN place_name VARCHAR(255) NULL AFTER description,
  ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

/* 7) Rafforza il NOT NULL su order_in_day */
ALTER TABLE activities
  MODIFY order_in_day INT NOT NULL;

/* 8) Indice unico per l'ordinamento nel giorno */
CREATE UNIQUE INDEX uk_activities_order ON activities(day_plan_id, order_in_day);

/* 9) (Opzionale) Togli l'indice temporaneo: la UNIQUE copre day_plan_id come prefisso */
DROP INDEX idx_activities_day ON activities;
