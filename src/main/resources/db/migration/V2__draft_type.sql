ALTER TABLE draft_document ADD COLUMN document_type VARCHAR NULL;
UPDATE draft_document SET document_type = 'default' WHERE document_type IS NULL;
ALTER TABLE draft_document ALTER COLUMN document_type SET NOT NULL;

ALTER TABLE draft_document DROP CONSTRAINT draft_document_user_id_key;
ALTER TABLE draft_document ADD CONSTRAINT draft_document_user_id_document_type_key UNIQUE (user_id, document_type);
