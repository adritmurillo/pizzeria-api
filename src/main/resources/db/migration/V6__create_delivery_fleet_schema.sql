CREATE TABLE delivery_drivers (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('E_BIKE', 'MOTORCYCLE', 'CAR')),
    max_capacity_pizzas INT NOT NULL CHECK (max_capacity_pizzas > 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'ON_DELIVERY', 'OFF_DUTY')),
    current_order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    completed_deliveries INT NOT NULL DEFAULT 0 CHECK (completed_deliveries >= 0),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_delivery_drivers_status ON delivery_drivers(status, completed_deliveries ASC);

ALTER TABLE orders ADD COLUMN IF NOT EXISTS assigned_driver_id UUID REFERENCES delivery_drivers(id) ON DELETE SET NULL;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS assigned_driver_name VARCHAR(100);

-- Flota inicial de repartidores con capacidades térmicas diferenciadas
INSERT INTO delivery_drivers (id, name, phone, vehicle_type, max_capacity_pizzas, status, current_order_id, completed_deliveries, version) VALUES
    ('d1111111-1111-1111-1111-111111111111', 'Mateo Rossi', '+51-999-100-201', 'E_BIKE', 4, 'AVAILABLE', NULL, 0, 0),
    ('d2222222-2222-2222-2222-222222222222', 'Valeria Silva', '+51-999-100-202', 'MOTORCYCLE', 8, 'AVAILABLE', NULL, 0, 0),
    ('d3333333-3333-3333-3333-333333333333', 'Diego Mendoza', '+51-999-100-203', 'CAR', 20, 'AVAILABLE', NULL, 0, 0)
ON CONFLICT (id) DO NOTHING;
