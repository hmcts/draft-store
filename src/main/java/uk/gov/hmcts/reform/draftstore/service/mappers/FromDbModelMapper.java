package uk.gov.hmcts.reform.draftstore.service.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

public final class FromDbModelMapper {

    private static final Logger logger = LoggerFactory.getLogger(FromDbModelMapper.class);

    public static Draft fromDb(
        uk.gov.hmcts.reform.draftstore.data.model.Draft dbDraft,
        Secrets secrets
    ) {
        String documentToReturn;

        try {
            documentToReturn = CryptoService.decrypt(dbDraft.encryptedDocument, secrets.primary);
        } catch (Exception exc) {
            if (secrets.secondary != null) {
                logger.info("Unable to decrypt using primary secret, retrying with secondary");
                documentToReturn = CryptoService.decrypt(dbDraft.encryptedDocument, secrets.secondary);
            } else {
                throw exc;
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

    private FromDbModelMapper() {
        // utility class constructor
    }
}
