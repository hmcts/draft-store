package uk.gov.hmcts.reform.draftstore.utils;

import java.util.function.Supplier;

public class IllegalArgument {

    public static void throwIf(Supplier<Boolean> condition, String message) {
        if (condition.get()) {
            throw new IllegalArgumentException(message);
        }
    }
}
