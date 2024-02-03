CREATE TABLE merchants (
    id SERIAL PRIMARY KEY,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    name VARCHAR(128)
);

INSERT INTO merchants (username, password, name)
VALUES
    ('12345', 'w/0MswEADDXnHwBhh8sa60ltUYLTMO86qvvPhw4DwLA=', 'Ivan Ivanov'),
    ('00000', 'WiKu6NMu7gM4WzRKfJ+0kfSRnZ776XoH3LbrcmTCMJE=', 'Sergey Sergeev');
