package uk.gov.hmcts.reform.draftstore.service.crypto;

public class InvalidKeyException extends RuntimeException {

    public InvalidKeyException(String message) {
        super(message);
    }
}
