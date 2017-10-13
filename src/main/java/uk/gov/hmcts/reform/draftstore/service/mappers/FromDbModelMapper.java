package uk.gov.hmcts.reform.draftstore.service.mappers;

import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;

public class FromDbModelMapper {

    public static Draft fromDb(
        uk.gov.hmcts.reform.draftstore.data.model.Draft dbDraft,
        String secret
    ) {
        // only during transition stage
        String documentToReturn =
            dbDraft.encryptedDocument == null
                ? dbDraft.document
                : CryptoService.decrypt(dbDraft.encryptedDocument, secret);

        return new Draft(
            dbDraft.id,
            documentToReturn,
            dbDraft.type,
            dbDraft.created,
            dbDraft.updated
        );
    }
}
