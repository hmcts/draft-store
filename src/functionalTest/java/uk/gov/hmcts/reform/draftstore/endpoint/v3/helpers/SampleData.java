package uk.gov.hmcts.reform.draftstore.endpoint.v3.helpers;

import uk.gov.hmcts.reform.draftstore.domain.Draft;

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
}
