package uk.gov.hmcts.reform.draftstore.service;

public class UserAndService {

    public final String userId;
    public final String service;
    public final String secret;

    public UserAndService(String userId, String service, String secret) {
        this.userId = userId;
        this.service = service;
        this.secret = secret;
    }

    public UserAndService(String userId, String service) {
        this(userId, service, null);
    }
}
