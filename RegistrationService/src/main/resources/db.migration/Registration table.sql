CREATE TABLE registration
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id  UUID,
    event_id UUID,
    timestamp TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_registration PRIMARY KEY (id)
);