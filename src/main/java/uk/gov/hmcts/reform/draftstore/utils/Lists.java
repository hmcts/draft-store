package uk.gov.hmcts.reform.draftstore.utils;

import java.util.List;
import java.util.Optional;

public class Lists {

    public static <T> Optional<T> last(List<T> list) {
        return Optional.ofNullable(list.isEmpty() ? null : list.get(list.size() - 1));
    }

    public static <T> Optional<T> safeGet(List<T> list, int index) {
        return index < list.size()
            ? Optional.ofNullable(list.get(index))
            : Optional.empty();
    }
}
