package uk.gov.hmcts.reform.draftstore.exception;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException() {
        this("FORBIDDEN");
    }

    public AuthorizationException(String message) {
        super(message);
    }
}
