DROP SCHEMA IF EXISTS cart CASCADE;
CREATE SCHEMA cart;


CREATE TABLE IF NOT EXISTS cart.cart (
    id          UUID PRIMARY KEY,
    username    VARCHAR(100) NOT NULL,
    is_active   BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cart.cart_product (
    id                  UUID PRIMARY KEY,
    product_id          UUID NOT NULL,
    quantity            INTEGER,
    shopping_cart_id    UUID NOT NULL REFERENCES cart.cart (id) ON DELETE CASCADE
);
