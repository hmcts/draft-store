ALTER TABLE draft_document ADD COLUMN service VARCHAR NULL;

-- only cmc uses draft store atm
UPDATE draft_document
SET service = 'cmc'
WHERE service IS NULL;

ALTER TABLE draft_document ALTER COLUMN service SET NOT NULL;
