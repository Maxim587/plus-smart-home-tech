DROP TABLE IF EXISTS product CASCADE;

CREATE TABLE IF NOT EXISTS product (
    id          UUID PRIMARY KEY,
    fragile     BOOLEAN,
    width       DOUBLE PRECISION NOT NULL,
    height      DOUBLE PRECISION NOT NULL,
    depth       DOUBLE PRECISION NOT NULL,
    weight      DOUBLE PRECISION NOT NULL,
    quantity    INT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_booking (
    id          UUID PRIMARY KEY,
    order_id    UUID UNIQUE NOT NULL,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS order_booking_product (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id          UUID NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    order_booking_id    UUID NOT NULL REFERENCES order_booking (id) ON DELETE CASCADE,
    quantity            INT
);