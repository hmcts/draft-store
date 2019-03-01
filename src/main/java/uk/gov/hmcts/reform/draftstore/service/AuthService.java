package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClient;
import uk.gov.hmcts.reform.draftstore.service.idam.User;
import uk.gov.hmcts.reform.draftstore.service.s2s.S2sClient;

@Service
public class AuthService {

    public static final String SERVICE_HEADER = "ServiceAuthorization";
    public static final String SECRET_HEADER = "Secret";

    private final IdamClient idamClient;
    private final S2sClient s2sClient;

    public AuthService(IdamClient idamClient, S2sClient s2sClient) {
        this.idamClient = idamClient;
        this.s2sClient = s2sClient;
    }

    public UserAndService authenticate(String userHeader, String serviceHeader) {
        return new UserAndService(
            idamClient.getUserDetails(userHeader).id,
            s2sClient.getServiceName(serviceHeader)
        );
    }

    public User authenticate(String userHeader) {
        return idamClient.getUserDetails(userHeader);
    }
}
