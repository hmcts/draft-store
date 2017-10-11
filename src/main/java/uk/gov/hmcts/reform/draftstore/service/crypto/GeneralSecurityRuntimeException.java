package uk.gov.hmcts.reform.draftstore.service.crypto;

import java.security.GeneralSecurityException;

public class GeneralSecurityRuntimeException extends RuntimeException {
    public GeneralSecurityRuntimeException(GeneralSecurityException cause) {
        super(cause);
    }
}
