CREATE TABLE card_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    card_number VARCHAR(16),
    exp_date VARCHAR(5),
    cvv VARCHAR(3),
    customer_id UUID REFERENCES customers(id)
);