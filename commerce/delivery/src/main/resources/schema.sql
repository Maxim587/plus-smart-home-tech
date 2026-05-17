DROP TABLE IF EXISTS delivery CASCADE;


CREATE TABLE IF NOT EXISTS address (
    id      UUID PRIMARY KEY,
    country VARCHAR(100),
    city    VARCHAR(50),
    street  VARCHAR(100),
    house   VARCHAR(50),
    flat    VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS delivery (
    id               UUID PRIMARY KEY,
    address_from_id  UUID REFERENCES address (id) ON DELETE CASCADE,
    address_to_id    UUID REFERENCES address (id) ON DELETE CASCADE,
    order_id         UUID UNIQUE NOT NULL,
    state            VARCHAR(50)
);
