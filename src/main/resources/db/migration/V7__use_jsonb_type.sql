ALTER TABLE draft_document
ALTER COLUMN document
SET DATA TYPE jsonb
USING document::jsonb;
