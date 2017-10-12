-- during the transition stage both columns will be nullable
ALTER TABLE draft_document
ADD COLUMN encrypted_document BYTEA NULL;

ALTER TABLE draft_document
ALTER COLUMN document DROP NOT NULL;
