CREATE TABLE draft_document (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  user_id VARCHAR(256) NOT NULL,
  document JSON NOT NULL,
  UNIQUE (user_id)
);