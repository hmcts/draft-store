package uk.gov.hmcts.reform.draftstore.service.s2s;

public class InvalidServiceTokenException extends RuntimeException {

    public InvalidServiceTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
