package uk.gov.hmcts.reform.draftstore.service.s2s;

import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;

import java.util.Optional;

import static uk.gov.hmcts.reform.draftstore.service.AuthService.SERVICE_HEADER;

public class S2sClientStub implements S2sClient {

    @Override
    public String getServiceName(String authHeader) {
        return Optional
            .ofNullable(authHeader)
            .filter(token -> !StringUtils.isEmpty(token))
            .orElseThrow(() -> new AuthorizationException(SERVICE_HEADER + " is required"));
    }
}
