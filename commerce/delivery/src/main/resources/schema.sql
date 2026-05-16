DROP TABLE IF EXISTS delivery CASCADE;


CREATE TABLE IF NOT EXISTS address (
    id      UUID PRIMARY KEY,
    country TEXT,
    city    TEXT,
    street  TEXT,
    house   TEXT,
    flat    TEXT
);

CREATE TABLE IF NOT EXISTS delivery (
    id               UUID PRIMARY KEY,
    address_from_id  UUID REFERENCES address (id) ON DELETE CASCADE,
    address_to_id    UUID REFERENCES address (id) ON DELETE CASCADE,
    order_id         UUID UNIQUE NOT NULL,
    state            VARCHAR(50)
);
