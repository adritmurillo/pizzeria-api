ALTER TABLE orders ADD COLUMN estimated_wait_minutes INT NOT NULL DEFAULT 0;
ALTER TABLE orders ADD COLUMN estimated_ready_at TIMESTAMP;
