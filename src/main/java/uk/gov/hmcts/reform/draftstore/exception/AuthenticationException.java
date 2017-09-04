package uk.gov.hmcts.reform.draftstore.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeException {
    private final HttpStatus statusCode;

    public AuthenticationException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
