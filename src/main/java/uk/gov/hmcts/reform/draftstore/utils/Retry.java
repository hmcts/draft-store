package uk.gov.hmcts.reform.draftstore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class Retry {
    private static final Logger LOGGER = LoggerFactory.getLogger(Retry.class);

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
                LOGGER.warn("Failed retry", exc);
            }
        }

        return function.apply(params.get(params.size() - 1));
    }
}
