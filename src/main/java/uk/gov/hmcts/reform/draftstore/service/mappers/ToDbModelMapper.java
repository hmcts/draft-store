package uk.gov.hmcts.reform.draftstore.service.mappers;

import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

import static uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService.encrypt;

public class ToDbModelMapper {

    public static CreateDraft toDb(
        uk.gov.hmcts.reform.draftstore.domain.CreateDraft draft,
        Secrets secrets
    ) {
        return new CreateDraft(
            null,
            encrypt(draft.document.toString(), secrets.primary),
            draft.type,
            draft.maxStaleDays
        );
    }

    public static UpdateDraft toDb(
        uk.gov.hmcts.reform.draftstore.domain.UpdateDraft draft,
        Secrets secrets
    ) {
        return new UpdateDraft(
            null,
            encrypt(draft.document.toString(), secrets.primary),
            draft.type
        );
    }
}
