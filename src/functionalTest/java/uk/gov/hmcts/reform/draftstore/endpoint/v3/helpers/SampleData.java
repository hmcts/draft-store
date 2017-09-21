package uk.gov.hmcts.reform.draftstore.endpoint.v3.helpers;

import uk.gov.hmcts.reform.draftstore.domain.Draft;

import java.time.LocalDateTime;

public class SampleData {
    public static Draft draft(String id) {
        return new Draft(
            id,
            "abc",
            "serviceA",
            "",
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
