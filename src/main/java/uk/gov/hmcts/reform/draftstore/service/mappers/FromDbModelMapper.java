package uk.gov.hmcts.reform.draftstore.service.mappers;

import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;
import uk.gov.hmcts.reform.draftstore.utils.Retry;

public class FromDbModelMapper {

    public static Draft fromDb(
        uk.gov.hmcts.reform.draftstore.data.model.Draft dbDraft,
        Secrets secrets
    ) {
        final String documentToReturn;

        // only during transition stage
        if (dbDraft.encryptedDocument == null) {
            documentToReturn = dbDraft.document;
        } else {
            documentToReturn =
                Retry.with(
                    secrets.getNonEmpty(),
                    s -> CryptoService.decrypt(dbDraft.encryptedDocument, s)
                );
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
