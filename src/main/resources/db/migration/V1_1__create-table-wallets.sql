CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount INTEGER,
    currency VARCHAR(64) NOT NULL,
    merchant_id UUID REFERENCES merchants(id)
);


INSERT INTO wallets (amount, currency, merchant_id)
    VALUES (10000, 'BRL', '290c014d-1df3-4544-a6a5-bc12dd35796d'),
           (75000, 'USD', '290c014d-1df3-4544-a6a5-bc12dd35796d'),
           (5000, 'BRL', 'c63804ed-a4bc-4ec5-a540-77de60809c2b'),
           (20000, 'USD', 'c63804ed-a4bc-4ec5-a540-77de60809c2b');