CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_method VARCHAR(64) NOT NULL,
    amount INTEGER NOT NULL,
    currency VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    type VARCHAR(64) NOT NULL,
    language VARCHAR(64),
    notification_url VARCHAR(256),
    customer_id UUID REFERENCES customers(id) NOT NULL,
    merchant_id UUID REFERENCES merchants(id) NOT NULL,
    wallet_id UUID REFERENCES wallets(id) NOT NULL,
    status VARCHAR(64),
    message VARCHAR(64)
);

