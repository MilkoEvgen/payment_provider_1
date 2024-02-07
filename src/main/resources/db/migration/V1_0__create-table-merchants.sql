CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE merchants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    secret_key VARCHAR(128) NOT NULL,
    name VARCHAR(128)
);

INSERT INTO merchants (id, secret_key, name)
VALUES
    ('290c014d-1df3-4544-a6a5-bc12dd35796d', 'w/0MswEADDXnHwBhh8sa60ltUYLTMO86qvvPhw4DwLA=', 'Ivan Ivanov'),
    ('c63804ed-a4bc-4ec5-a540-77de60809c2b', 'WiKu6NMu7gM4WzRKfJ+0kfSRnZ776XoH3LbrcmTCMJE=', 'Sergey Sergeev');
