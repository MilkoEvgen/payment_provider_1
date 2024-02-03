CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    payment_method VARCHAR(64),
    amount INTEGER,
    currency VARCHAR(64),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    type VARCHAR(64),
    card_data_id INTEGER REFERENCES card_data(id),
    language VARCHAR(64),
    notification_url VARCHAR(256),
    customer_id INTEGER REFERENCES customers(id),
    merchant_id INTEGER REFERENCES merchants(id),
    status VARCHAR(64),
    message VARCHAR(64)
);

