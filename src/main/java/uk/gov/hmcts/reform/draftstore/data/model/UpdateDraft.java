package uk.gov.hmcts.reform.draftstore.data.model;

public class UpdateDraft {

    public final String document;
    public final byte[] encryptedDocument;
    public final String type;

    // region constructor
    public UpdateDraft(
        String document,
        byte[] encryptedDocument,
        String type
    ) {
        this.document = document;
        this.encryptedDocument = encryptedDocument;
        this.type = type;
    }
    // endregion
}
