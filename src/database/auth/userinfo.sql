CREATE TABLE auth.userinfo
(
    id VARCHAR(64) NOT NULL,
    family_name VARCHAR(256) NULL,
    gender VARCHAR(8) NULL,
    given_name VARCHAR(256) NULL,
    link VARCHAR(1024) NULL,
    locale CHAR(4) NULL,
    name VARCHAR(512) NULL,
    picture VARCHAR(1024) NULL
);