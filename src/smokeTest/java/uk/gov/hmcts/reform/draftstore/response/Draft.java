package uk.gov.hmcts.reform.draftstore.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.ZonedDateTime;

/*
 * This class should encapsulate functionality (OO) and not expose its internals "public final"
 * We need to refactor this to follow good ood principles and practice.
 * Jira ticket "RPE-933"
 */
@SuppressWarnings("PMD.DataClass")
public class Draft {

    public final String id;
    public final JsonNode document;
    public final String type;
    public final ZonedDateTime created;
    public final ZonedDateTime updated;

    public Draft(
        @JsonProperty("id") String id,
        @JsonProperty("document") JsonNode document,
        @JsonProperty("type") String type,
        @JsonProperty("created") ZonedDateTime created,
        @JsonProperty("updated") ZonedDateTime updated
    ) {
        this.id = id;
        this.document = document;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }
}
