package uk.gov.hmcts.reform.draftstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class Draft {

    public final int id;

    @JsonIgnore
    public final String userId;

    @JsonRawValue
    public final String document;

    public final String type;

    public Draft(int id, String userId, String document, String type) {
        this.id = id;
        this.userId = userId;
        this.document = document;
        this.type = type;
    }
}
