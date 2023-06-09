-- DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS users, item_request, items, booking, comments CASCADE;



CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS item_request (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requestor BIGINT NOT NULL,
    description text,
    created timestamp (3) WITHOUT TIME ZONE,
    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT fk_item_request_to_users FOREIGN KEY(requestor) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    owner BIGINT NOT NULL,
    name VARCHAR(255),
    description text,
    is_available boolean,
    request_id BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner) REFERENCES users(id),
    CONSTRAINT fk_items_to_item_request FOREIGN KEY(request_id) REFERENCES item_request(id)
    );

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    booking_start TIMESTAMP (3) WITHOUT TIME ZONE,
    booking_end TIMESTAMP (3) WITHOUT TIME ZONE,
    status varchar(10),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_to_items FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_booking_to_booker FOREIGN KEY(booker_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    comment_text text NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    date_created TIMESTAMP (3) WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id)
    );