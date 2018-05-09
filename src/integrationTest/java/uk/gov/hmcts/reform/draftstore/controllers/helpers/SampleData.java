package uk.gov.hmcts.reform.draftstore.controllers.helpers;

import com.google.common.base.Strings;
import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.data.model.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

import java.time.ZonedDateTime;

public final class SampleData {
    public static Draft draft(String id) {
        return new Draft(
            id,
            "abc",
            "serviceA",
            "{}",
            null,
            "some_type",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        );
    }

    public static UpdateDraft updateDraft() {
        return new UpdateDraft("{}", null, "some_type");
    }

    public static CreateDraft createDraft(Integer maxStaleDays) {
        return new CreateDraft(
            "{}",
            null,
            "some_type",
            maxStaleDays
        );
    }

    public static String secret() {
        return Strings.repeat("x", Secrets.MIN_SECRET_LENGTH);
    }

    private SampleData() {
        // utility class constructor
    }
}
