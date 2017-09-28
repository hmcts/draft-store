ALTER TABLE draft_document
ADD COLUMN created TIMESTAMP NULL;

UPDATE draft_document
SET created = now();

ALTER TABLE draft_document
ALTER COLUMN created SET NOT NULL;



ALTER TABLE draft_document
ADD COLUMN updated TIMESTAMP NULL;

UPDATE draft_document
SET updated = now();

ALTER TABLE draft_document
ALTER COLUMN updated SET NOT NULL;
