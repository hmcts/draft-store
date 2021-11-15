-- get drafts for a user and service index
CREATE INDEX draft_document_user_id_service_idx on draft_document (user_id, service);
