package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.draftstore.utils.Lists;

import java.util.List;

public class DraftList {

    public final List<Draft> data;

    @JsonProperty("paging_cursors")
    public final PagingCursors paging;

    public DraftList(List<Draft> data, PagingCursors paging) {
        this.data = data;
        this.paging = paging;
    }

    public DraftList(List<Draft> data) {
        this(
            data,
            new PagingCursors(
                Lists.last(data).map(last -> last.id).orElse(null)
            )
        );
    }

    static class PagingCursors {
        public final String after;

        public PagingCursors(String after) {
            this.after = after;
        }
    }
}
