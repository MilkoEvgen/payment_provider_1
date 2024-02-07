CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(128),
    last_name VARCHAR(128),
    country VARCHAR(64)
);