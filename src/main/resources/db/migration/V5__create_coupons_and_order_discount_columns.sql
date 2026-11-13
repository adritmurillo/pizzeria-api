CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    discount_value NUMERIC(10, 2) NOT NULL CHECK (discount_value > 0),
    min_order_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (min_order_amount >= 0),
    min_pizza_count INT NOT NULL DEFAULT 0 CHECK (min_pizza_count >= 0),
    max_uses INT NOT NULL CHECK (max_uses > 0),
    current_uses INT NOT NULL DEFAULT 0 CHECK (current_uses >= 0 AND current_uses <= max_uses),
    valid_until TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_coupons_code ON coupons(code);

ALTER TABLE orders ADD COLUMN IF NOT EXISTS subtotal_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS applied_coupon_code VARCHAR(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00;

UPDATE orders SET subtotal_amount = total_amount WHERE subtotal_amount = 0.00;

-- Cupones promocionales iniciales (Specification Pattern)
INSERT INTO coupons (id, code, discount_type, discount_value, min_order_amount, min_pizza_count, max_uses, current_uses, valid_until, active, version) VALUES
    ('c1111111-1111-1111-1111-111111111111', 'WELCOME10', 'PERCENTAGE', 10.00, 20.00, 1, 100, 0, '2027-12-31 23:59:59', TRUE, 0),
    ('c2222222-2222-2222-2222-222222222222', 'FIESTA25', 'PERCENTAGE', 25.00, 45.00, 3, 50, 0, '2027-12-31 23:59:59', TRUE, 0),
    ('c3333333-3333-3333-3333-333333333333', 'PIZZA5OFF', 'FIXED_AMOUNT', 5.00, 25.00, 2, 200, 0, '2027-12-31 23:59:59', TRUE, 0)
ON CONFLICT (id) DO NOTHING;
