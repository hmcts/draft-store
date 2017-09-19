package uk.gov.hmcts.reform.draftstore.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.draftstore.exception.AuthorizationException;
import uk.gov.hmcts.reform.draftstore.service.idam.IdamClient;
import uk.gov.hmcts.reform.draftstore.service.idam.User;

import java.util.Optional;
import javax.validation.constraints.NotNull;

/*
TODO: implementation of this service should be replaced with a request to the IDAM /details service (to get the userid)
The auth header should have a Bearer JWT token.
For tactical reasons, no IDAM stubs/not deployed anywhere, Divorce BA's decided to go with this solution.
Initially the implementation did indeed send a request to IDAM's /details endpoint to get the documenter's user id
@see http://git.reform/divorce/draft-document-store/commit/91839f84e54e42f5b70f28c68a150ad60ef86b9e
 */
@Service
public class AuthService {

    public static final String SERVICE_HEADER = "ServiceAuthorization";
    public static final String AUTH_TYPE = "hmcts-id ";

    public final IdamClient idamClient;

    public AuthService(IdamClient idamClient) {
        this.idamClient = idamClient;
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

    public String getUserId(@NotNull String authHeader) {
        return idamClient.getUserDetails(authHeader).id;
    }

    public String getServiceName(@NotNull String serviceToken) {
        // TODO: use jwt tokens
        return Optional
            .ofNullable(serviceToken)
            .filter(token -> !StringUtils.isEmpty(token))
            .orElseThrow(() -> new AuthorizationException(SERVICE_HEADER + " is required"));
    }
}
