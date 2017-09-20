package uk.gov.hmcts.reform.draftstore.service.idam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    public final String id;
    public final String email;

    public User(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email
    ) {
        this.id = id;
        this.email = email;
    }
}
