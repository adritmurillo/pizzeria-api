CREATE TABLE IF NOT EXISTS pizzas (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(38, 2) NOT NULL,
    size VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL,
    CONSTRAINT pizzas_size_check CHECK (size IN ('SMALL', 'MEDIUM','LARGE')),
    CONSTRAINT pizzas_category_check CHECK (category IN ('CLASSIC','SPECIALITY', 'VEGETARIAN'))
    );


CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT orders_status_check CHECK (status IN ('PENDING','PREPARING', 'READY', 'DELIVERED', 'CANCELLED'))
    );

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    pizza_id UUID NOT NULL,
    pizza_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
    );

CREATE INDEX IF NOT EXISTS idx_orders_status_created_at ON
    orders(status, created_at ASC);

CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at
    ASC);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON
    order_items(order_id);