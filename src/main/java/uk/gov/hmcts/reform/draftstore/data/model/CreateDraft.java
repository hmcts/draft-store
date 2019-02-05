package uk.gov.hmcts.reform.draftstore.data.model;

/*
 * This class should encapsulate functionality (OO) and not expose its internals "public final"
 * We need to refactor this to follow good ood principles and practice.
 * Jira ticket "RPE-933"
 */
@SuppressWarnings("PMD.DataClass")
public class CreateDraft {

    public final String document;
    public final byte[] encryptedDocument;
    public final String type;
    public final Integer maxStaleDays;

    // region constructor
    public CreateDraft(String document, byte[] encryptedDocument, String type, Integer maxStaleDays) {
        this.document = document;
        this.encryptedDocument = encryptedDocument;
        this.type = type;
        this.maxStaleDays = maxStaleDays;
    }
    // endregion
}
