-- Aggiungi colonna per l’ordinamento dei trip per owner
ALTER TABLE trips
 ADD COLUMN order_in_owner INT NOT NULL DEFAULT 0;

-- Inizializza con l'id (va benissimo per partire)
UPDATE trips SET order_in_owner = id WHERE order_in_owner = 0;

-- Unicità (owner_id, order_in_owner)
CREATE UNIQUE INDEX uq_trips_owner_order
 ON trips(owner_id, order_in_owner);