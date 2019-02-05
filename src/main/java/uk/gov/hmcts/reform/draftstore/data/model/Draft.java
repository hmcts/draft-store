package uk.gov.hmcts.reform.draftstore.data.model;

import java.time.ZonedDateTime;

/*
 * This class should encapsulate functionality (OO) and not expose its internals "public final"
 * We need to refactor this to follow good ood principles and practice.
 * Jira ticket "RPE-933"
 */
@SuppressWarnings("PMD.DataClass")
public class Draft {

    public final String id;
    public final String userId;
    public final String service;
    public final String document;
    public final byte[] encryptedDocument;
    public final String type;
    public final ZonedDateTime created;
    public final ZonedDateTime updated;

    // region constructor

    public Draft(
        String id,
        String userId,
        String service,
        String document,
        byte[] encryptedDocument,
        String type,
        ZonedDateTime created,
        ZonedDateTime updated
    ) {
        this.id = id;
        this.userId = userId;
        this.service = service;
        this.document = document;
        this.encryptedDocument = encryptedDocument;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }

    // endregion
}
