CREATE TABLE webhooks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    url VARCHAR(256),
    date_time TIMESTAMP NOT NULL,
    status_code INT,
    response VARCHAR(256)
);