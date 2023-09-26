package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.draftstore.service.validation.JsonObject;

public class CreateDraft {

    @JsonObject
    public final JsonNode document;

    @NotNull
    public final String type;

    @Schema(
        name = "max_stale_days",
        description = "Number of days before removing a draft that hasn't been updated"
    )
    @Min(value = 1L)
    public final Integer maxStaleDays;

    public CreateDraft(
        @JsonProperty("document") JsonNode document,
        @JsonProperty("type") String type,
        @JsonProperty("max_age") Integer maxStaleDays
    ) {
        this.document = document;
        this.type = type;
        this.maxStaleDays = maxStaleDays;
    }
}
