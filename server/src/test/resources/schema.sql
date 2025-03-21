CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    512
) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY
(
    id
),
    CONSTRAINT UQ_USER_EMAIL UNIQUE
(
    email
)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id
    INT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    owner_id
    INT
    NOT
    NULL,
    description
    VARCHAR
    NOT
    NULL,
    created
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    FOREIGN
    KEY
(
    owner_id
) REFERENCES users
(
    id
) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS items
(
    id
    INT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    owner_id
    INT
    NOT
    NULL,
    request_id
    INT,
    name
    VARCHAR
    NOT
    NULL,
    description
    VARCHAR
    NOT
    NULL,
    available
    BOOLEAN,
    FOREIGN
    KEY
(
    owner_id
) REFERENCES users
(
    id
) ON DELETE CASCADE,
    FOREIGN KEY
(
    request_id
) REFERENCES requests
(
    id
)
  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL,
    booker_id
    BIGINT
    NOT
    NULL,
    start_date
    TIMESTAMP
    NOT
    NULL,
    end_date
    TIMESTAMP
    NOT
    NULL,
    status
    VARCHAR
(
    10
) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY
(
    id
),
    FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
),
    FOREIGN KEY
(
    booker_id
) REFERENCES users
(
    id
)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL,
    user_id
    BIGINT
    NOT
    NULL,
    text
    VARCHAR
(
    1000
) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_comment PRIMARY KEY
(
    id
),
    FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
),
    FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
)
    );