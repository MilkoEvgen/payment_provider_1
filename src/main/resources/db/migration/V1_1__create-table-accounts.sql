CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    amount INTEGER,
    currency VARCHAR(64) NOT NULL,
    merchant_id INT REFERENCES merchants(id)
);

INSERT INTO accounts (amount, currency, merchant_id)
    VALUES (10000, 'BRL', 1),
           (75000, 'USD', 1),
           (5000, 'BRL', 2),
           (20000, 'USD', 2);