
CREATE TABLE ticket
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id   BIGINT,
    event_id  BIGINT,
    paid_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_ticket PRIMARY KEY (id)
);
