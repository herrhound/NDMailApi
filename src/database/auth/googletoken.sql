CREATE TABLE auth.googletoken
(
  access_token varchar(128) NOT NULL PRIMARY KEY,
  expires_in Int NOT NULL,
  id_token varchar(4096) NOT NULL,
  refresh_token varchar(128) NOT NULL,
  token_type varchar(128) NOT NULL,
  userinfo_id varchar(64) NOT NULL,
  issued_on timestamp DEFAULT (current_timestamp) NOT NULL
);