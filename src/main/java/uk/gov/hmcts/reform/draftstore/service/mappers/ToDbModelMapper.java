package uk.gov.hmcts.reform.draftstore.service.mappers;

import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.UpdateDraft;

import static uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService.encrypt;

public class ToDbModelMapper {

    public static CreateDraft toDb(
        uk.gov.hmcts.reform.draftstore.domain.CreateDraft draft,
        String secret
    ) {
        return new CreateDraft(
            secret == null ? draft.document.toString() : null,
            secret == null ? null : encrypt(draft.document.toString(), secret),
            draft.type,
            draft.maxStaleDays
        );
    }

    public static UpdateDraft toDb(
        uk.gov.hmcts.reform.draftstore.domain.UpdateDraft draft,
        String secret
    ) {
        return new UpdateDraft(
            secret == null ? draft.document.toString() : null,
            secret == null ? null : encrypt(draft.document.toString(), secret),
            draft.type
        );
    }
}
