DROP SCHEMA IF EXISTS warehouse CASCADE;
CREATE SCHEMA warehouse;

CREATE TABLE IF NOT EXISTS warehouse.product (
    id          UUID PRIMARY KEY,
    fragile     BOOLEAN,
    width       DOUBLE PRECISION NOT NULL,
    height      DOUBLE PRECISION NOT NULL,
    depth       DOUBLE PRECISION NOT NULL,
    weight      DOUBLE PRECISION NOT NULL,
    quantity    INT NOT NULL
);