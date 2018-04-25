package uk.gov.hmcts.reform.draftstore.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DraftList {

    public final List<Draft> data;
    public final PagingCursors paging;

    public DraftList(
        @JsonProperty("data") List<Draft> data,
        @JsonProperty("paging_cursors") PagingCursors paging
    ) {
        this.data = data;
        this.paging = paging;
    }

    static class PagingCursors {
        public final String after;

        public PagingCursors(@JsonProperty("after") String after) {
            this.after = after;
        }
    }
}
