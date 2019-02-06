package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.time.ZonedDateTime;

public class Draft {

    public final String id;

    @JsonRawValue
    public final String document;

    public final String type;

    public final ZonedDateTime created;

    public final ZonedDateTime updated;

    // region constructor
    public Draft(
        String id,
        String document,
        String type,
        ZonedDateTime created,
        ZonedDateTime updated
    ) {
        this.id = id;
        this.document = document;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }
    // endregion
}
