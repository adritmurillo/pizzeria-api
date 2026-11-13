CREATE TABLE ingredients (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    unit VARCHAR(20) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    cost_per_unit NUMERIC(10, 2) NOT NULL CHECK (cost_per_unit >= 0),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE pizza_recipes (
    id UUID PRIMARY KEY,
    pizza_id UUID NOT NULL REFERENCES pizzas(id) ON DELETE CASCADE,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    quantity_required INT NOT NULL CHECK (quantity_required > 0),
    unit VARCHAR(20) NOT NULL,
    CONSTRAINT uq_pizza_ingredient UNIQUE (pizza_id, ingredient_id)
);

CREATE INDEX idx_pizza_recipes_pizza_id ON pizza_recipes(pizza_id);
CREATE INDEX idx_pizza_recipes_ingredient_id ON pizza_recipes(ingredient_id);

-- Insumos estándar iniciales
INSERT INTO ingredients (id, name, unit, stock, cost_per_unit, version) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Queso Mozzarella', 'GRAMS', 10000, 0.02, 0),
    ('22222222-2222-2222-2222-222222222222', 'Salsa de Tomate', 'MILLILITERS', 5000, 0.01, 0),
    ('33333333-3333-3333-3333-333333333333', 'Masa Fresca', 'UNITS', 100, 0.50, 0),
    ('44444444-4444-4444-4444-444444444444', 'Carne Molida Especial', 'GRAMS', 4000, 0.03, 0)
ON CONFLICT (id) DO NOTHING;

-- Seed idempotente de Pizza Brava para garantizar integridad referencial en contenedores limpios
INSERT INTO pizzas (id, name, description, price, size, category, available, stock, version) VALUES
    ('c0825f38-bced-40dc-a8a6-ad0db4387ff3', 'Pizza Brava', 'Pizza especial con carne molida, queso mozzarella y salsa de tomate artesanal', 35.00, 'MEDIUM', 'SPECIALITY', true, 50, 0)
ON CONFLICT (id) DO NOTHING;

-- Receta oficial para Pizza Brava
INSERT INTO pizza_recipes (id, pizza_id, ingredient_id, quantity_required, unit) VALUES
    ('a1111111-1111-1111-1111-111111111111', 'c0825f38-bced-40dc-a8a6-ad0db4387ff3', '33333333-3333-3333-3333-333333333333', 1, 'UNITS'),
    ('a2222222-2222-2222-2222-222222222222', 'c0825f38-bced-40dc-a8a6-ad0db4387ff3', '11111111-1111-1111-1111-111111111111', 200, 'GRAMS'),
    ('a3333333-3333-3333-3333-333333333333', 'c0825f38-bced-40dc-a8a6-ad0db4387ff3', '22222222-2222-2222-2222-222222222222', 100, 'MILLILITERS'),
    ('a4444444-4444-4444-4444-444444444444', 'c0825f38-bced-40dc-a8a6-ad0db4387ff3', '44444444-4444-4444-4444-444444444444', 80, 'GRAMS')
ON CONFLICT (id) DO NOTHING;
