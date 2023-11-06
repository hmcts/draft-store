package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.draftstore.service.validation.JsonObject;

public class UpdateDraft {

    @JsonObject
    public final JsonNode document;

    @NotNull
    public final String type;

    public UpdateDraft(
        @JsonProperty("document") JsonNode document,
        @JsonProperty("type") String type
    ) {
        this.document = document;
        this.type = type;
    }
}
