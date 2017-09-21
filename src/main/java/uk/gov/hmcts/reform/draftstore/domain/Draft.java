package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Draft {

    public final String id;

    @JsonIgnore
    public final String userId;

    @JsonIgnore
    public final String service;

    @JsonRawValue
    public final String document;

    public final String type;

    public final ZonedDateTime created;

    public final ZonedDateTime updated;

    // region constructor
    public Draft(
        String id,
        String userId,
        String service,
        String document,
        String type,
        ZonedDateTime created,
        ZonedDateTime updated
    ) {
        this.id = id;
        this.userId = userId;
        this.service = service;
        this.document = document;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }
    // endregion
}
