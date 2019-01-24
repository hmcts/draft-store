package uk.gov.hmcts.reform.draftstore.controllers.internal;

public class HttpStatusResponse {
    private String message;

    public HttpStatusResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
