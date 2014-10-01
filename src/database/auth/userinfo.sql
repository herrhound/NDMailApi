CREATE TABLE auth.userinfo
(
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    family_name VARCHAR(256) NOT NULL,
    gender VARCHAR(8) NOT NULL,
    given_name VARCHAR(256) NOT NULL,
    link VARCHAR(1024) NOT NULL,
    locale CHAR(4) NOT NULL,
    name VARCHAR(512) NOT NULL,
    picture VARCHAR(1024) NOT NULL
);