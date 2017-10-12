package uk.gov.hmcts.reform.draftstore.service.mappers;

import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.UpdateDraft;

import java.util.function.Supplier;

import static uk.gov.hmcts.reform.draftstore.service.crypto.CryptoService.encrypt;

public class ToDbModelMapper {

    public static CreateDraft toDb(
        uk.gov.hmcts.reform.draftstore.domain.CreateDraft draft,
        String secret
    ) {
        Docs docs = getDocuments(secret, () -> draft.document.toString());

        return new CreateDraft(
            docs.plainText,
            docs.encrypted,
            draft.type,
            draft.maxStaleDays
        );
    }

    public static UpdateDraft toDb(
        uk.gov.hmcts.reform.draftstore.domain.UpdateDraft draft,
        String secret
    ) {
        Docs docs = getDocuments(secret, () -> draft.document.toString());

        return new UpdateDraft(
            docs.plainText,
            docs.encrypted,
            draft.type
        );
    }

    private static Docs getDocuments(String secret, Supplier<String> inputDocument) {
        return secret != null
            ? new Docs(null, encrypt(inputDocument.get(), secret))
            : new Docs(inputDocument.get(), null);
    }

    static class Docs {
        String plainText;
        byte[] encrypted;

        public Docs(String plainText, byte[] encrypted) {
            this.plainText = plainText;
            this.encrypted = encrypted;
        }
    }
}
