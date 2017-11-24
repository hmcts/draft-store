package uk.gov.hmcts.reform.draftstore.service.mappers;

import com.google.common.base.Strings;
import uk.gov.hmcts.reform.draftstore.service.secrets.Secrets;

public class SampleSecret {
    public static String get() {
        return Strings.repeat("x", Secrets.MIN_SECRET_LENGTH);
    }

    public static Secrets getObject() {
        return new Secrets(get(), null);
    }
}
