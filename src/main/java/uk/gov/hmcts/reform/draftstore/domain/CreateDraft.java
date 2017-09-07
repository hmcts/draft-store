package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import uk.gov.hmcts.reform.draftstore.service.validation.JsonObject;

import javax.validation.constraints.NotNull;

public class CreateDraft {

    @JsonObject
    public final JsonNode document;

    @NotNull
    public final String type;

    public CreateDraft(
        @JsonProperty("document") JsonNode document,
        @JsonProperty("type") String type
    ) {
        this.document = document;
        this.type = type;
    }
}
