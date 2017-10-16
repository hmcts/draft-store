package uk.gov.hmcts.reform.draftstore.service.mappers;

import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

public class FromDbModelMapper {

    public static Draft fromDb(
        uk.gov.hmcts.reform.draftstore.data.model.Draft dbDraft,
        Secrets secrets
    ) {
        String documentToReturn;

        // only during transition stage
        if (dbDraft.encryptedDocument == null) {
            documentToReturn = dbDraft.document;
        } else {
            try {
                documentToReturn = CryptoService.decrypt(dbDraft.encryptedDocument, secrets.primary);
            } catch (Exception exc) {
                if (secrets.secondary != null) {
                    documentToReturn = CryptoService.decrypt(dbDraft.encryptedDocument, secrets.secondary);
                } else {
                    throw exc;
                }
            }
        }

        return new Draft(
            dbDraft.id,
            documentToReturn,
            dbDraft.type,
            dbDraft.created,
            dbDraft.updated
        );
    }
}
