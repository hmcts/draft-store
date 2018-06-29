package uk.gov.hmcts.reform.draftstore.service.idam;

public class InvalidIdamTokenException extends RuntimeException {

    public InvalidIdamTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
