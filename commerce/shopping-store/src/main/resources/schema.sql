DROP SCHEMA IF EXISTS store CASCADE;
CREATE SCHEMA store;

CREATE TABLE IF NOT EXISTS store.product (
    id              UUID PRIMARY KEY,
    name            VARCHAR(1000) NOT NULL,
    description     VARCHAR(10000) NOT NULL,
    image_src       VARCHAR(2000),
    quantity_state  VARCHAR(100) NOT NULL,
    state           VARCHAR(100) NOT NULL,
    category        VARCHAR(100),
    price           NUMERIC(10, 2) NOT NULL CHECK (price >= 1)
);
