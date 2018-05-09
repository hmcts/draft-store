package uk.gov.hmcts.reform.draftstore.data.model;

public class UpdateDraft {

    public final byte[] encryptedDocument;
    public final String type;

    // region constructor
    public UpdateDraft(
        byte[] encryptedDocument,
        String type
    ) {
        this.encryptedDocument = encryptedDocument;
        this.type = type;
    }
    // endregion
}
