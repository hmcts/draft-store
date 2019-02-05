package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.time.ZonedDateTime;

/*
 * This class should encapsulate functionality (OO) and not expose its internals "public final"
 * We need to refactor this to follow good ood principles and practice.
 * Jira ticket "RPE-933"
 */
@SuppressWarnings("PMD.DataClass")
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
