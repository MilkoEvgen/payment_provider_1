CREATE TABLE card_data (
    id SERIAL PRIMARY KEY,
    card_number VARCHAR(16),
    exp_date VARCHAR(5),
    cvv VARCHAR(3)
);