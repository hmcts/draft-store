package uk.gov.hmcts.reform.draftstore.utils;

import java.util.List;
import java.util.function.Function;

public class Retry {

    /**
     * Retries calling passed function with passed parameters.
     *
     * @return The first successful result of calling a function.
     */
    public static <T, R> R with(
        List<T> params,
        Function<T, R> function
    ) {
        IllegalArgument.throwIf(() -> params.isEmpty(), "params list must not be empty");

        for (int i = 0; i < params.size() - 1; i++) {
            try {
                return function.apply(params.get(i));
            } catch (RuntimeException exc) {

            }
        }

        return function.apply(params.get(params.size() - 1));
    }
}
