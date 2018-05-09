package uk.gov.hmcts.reform.draftstore.data.model;

import java.time.ZonedDateTime;

public class Draft {

    public final String id;
    public final String userId;
    public final String service;
    public final byte[] encryptedDocument;
    public final String type;
    public final ZonedDateTime created;
    public final ZonedDateTime updated;

    // region constructor

    public Draft(
        String id,
        String userId,
        String service,
        byte[] encryptedDocument,
        String type,
        ZonedDateTime created,
        ZonedDateTime updated
    ) {
        this.id = id;
        this.userId = userId;
        this.service = service;
        this.encryptedDocument = encryptedDocument;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }

    // endregion
}
