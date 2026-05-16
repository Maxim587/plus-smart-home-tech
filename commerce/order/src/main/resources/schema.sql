DROP TABLE IF EXISTS orders CASCADE;


CREATE TABLE IF NOT EXISTS orders (
    id               UUID PRIMARY KEY,
    username         VARCHAR(100) NOT NULL,
    shopping_cart_id UUID,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR(255),
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,
    total_price      NUMERIC(10, 2),
    delivery_price   NUMERIC(10, 2),
    product_price    NUMERIC(10, 2)
);

CREATE INDEX IF NOT EXISTS IDX_ORDER_USERNAME ON orders (username);

CREATE TABLE IF NOT EXISTS order_product (
    id               UUID PRIMARY KEY,
    product_id       UUID NOT NULL,
    quantity         INTEGER,
    order_id         UUID NOT NULL REFERENCES orders (id) ON DELETE CASCADE
);