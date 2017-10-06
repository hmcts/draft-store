package uk.gov.hmcts.reform.draftstore.utils;

import java.util.List;
import java.util.Optional;

public class Lists {

    public static <T> Optional<T> last(List<T> list) {
        return Optional.ofNullable(list.isEmpty() ? null : list.get(list.size() - 1));
    }
}
