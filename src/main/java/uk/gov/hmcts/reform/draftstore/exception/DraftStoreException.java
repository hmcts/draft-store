package uk.gov.hmcts.reform.draftstore.exception;

public class DraftStoreException extends RuntimeException {
    public DraftStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
