DROP TABLE IF EXISTS cart CASCADE;


CREATE TABLE IF NOT EXISTS cart (
    id          UUID PRIMARY KEY,
    username    VARCHAR(100) NOT NULL,
    is_active   BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_product (
    id                  UUID PRIMARY KEY,
    product_id          UUID NOT NULL,
    quantity            INTEGER,
    shopping_cart_id    UUID NOT NULL REFERENCES cart (id) ON DELETE CASCADE
);
