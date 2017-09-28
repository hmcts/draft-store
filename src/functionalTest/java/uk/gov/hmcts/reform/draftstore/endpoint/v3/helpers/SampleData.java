package uk.gov.hmcts.reform.draftstore.endpoint.v3.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;

import java.time.ZonedDateTime;

public class SampleData {
    public static Draft draft(String id) {
        return new Draft(
            id,
            "abc",
            "serviceA",
            "{}",
            "some_type",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        );
    }

    public static UpdateDraft updateDraft() {
        return new UpdateDraft(new ObjectMapper().createObjectNode(), "some_type");
    }

    public static CreateDraft createDraft(int maxStaleDays) {
        return new CreateDraft(
            new ObjectMapper().createObjectNode(),
            "some_type",
            maxStaleDays
        );
    }
}
