CREATE TABLE IF NOT EXISTS house
(
    house_id          INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id           INT          NOT NULL,
    house_name        VARCHAR(256) NOT NULL,
    house_type        VARCHAR(32),
    address           VARCHAR(256),
    image_url         VARCHAR(256),
    price_per_month   INT          NOT NULL,
    status            VARCHAR(32),
    description       VARCHAR(1024),
    created_date      TIMESTAMP    NOT NULL,
    last_update_date  TIMESTAMP    NOT NULL
    );
