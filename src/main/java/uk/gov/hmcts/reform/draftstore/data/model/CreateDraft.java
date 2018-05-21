package uk.gov.hmcts.reform.draftstore.data.model;

public class CreateDraft {

    public final byte[] encryptedDocument;
    public final String type;
    public final Integer maxStaleDays;

    // region constructor
    public CreateDraft(byte[] encryptedDocument, String type, Integer maxStaleDays) {
        this.encryptedDocument = encryptedDocument;
        this.type = type;
        this.maxStaleDays = maxStaleDays;
    }
    // endregion
}
