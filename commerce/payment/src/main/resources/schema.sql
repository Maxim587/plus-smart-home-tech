DROP TABLE IF EXISTS payment CASCADE;


CREATE TABLE IF NOT EXISTS payment (
    id               UUID PRIMARY KEY,
    state            VARCHAR(50) NOT NULL,
    total_payment    NUMERIC(10, 2),
    delivery_total   NUMERIC(10, 2),
    fee_total        NUMERIC(10, 2),
    order_id         UUID
);

