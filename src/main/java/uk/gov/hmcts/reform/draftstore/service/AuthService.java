package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClient;
import uk.gov.hmcts.reform.draftstore.service.s2s.S2sClient;

import javax.validation.constraints.NotNull;

@Service
public class AuthService {

    public static final String SERVICE_HEADER = "ServiceAuthorization";
    public static final String SECRET_HEADER = "Secret";
    public static final String AUTH_TYPE = "hmcts-id ";

    private final IdamClient idamClient;
    private final S2sClient s2sClient;

    public AuthService(IdamClient idamClient, S2sClient s2sClient) {
        this.idamClient = idamClient;
        this.s2sClient = s2sClient;
    }

    @Deprecated
    public String userIdFromAuthToken(@NotNull String authToken) {
        if (authToken.startsWith(AUTH_TYPE)) {
            String userId = authToken.replace(AUTH_TYPE, "");
            if (!StringUtils.isEmpty(userId)) {
                return userId;
            }
        }

        throw new AuthorizationException(
            "Authorization token must be given in following format: '" + AUTH_TYPE + "<userId>'"
        );
    }

    public UserAndService authenticate(String userHeader, String serviceHeader, String secretHeader) {
        return new UserAndService(
            idamClient.getUserDetails(userHeader).id,
            s2sClient.getServiceName(serviceHeader),
            secretHeader
        );
    }
}
